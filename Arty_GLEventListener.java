import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
public class Arty_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
  private float aspect;
    
  public Arty_GLEventListener(Camera camera) {
    this.camera = camera;
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    aspect = (float)width/(float)height;
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    disposeMeshes(gl);
  }

  // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  // ***************************************************
  /* An array of random numbers
   */ 
  
  private int NUM_RANDOMS = 1000;
  private float[] randoms;
  
  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i=0; i<NUM_RANDOMS; ++i) {
      randoms[i] = (float)Math.random();
    }
  }
  
  // ***************************************************
  /* INTERACTION
   *
   *
   */
   
  private boolean animation = false;
  private boolean animationIndexX = false, animationMiddleX = false, animationRingX = false, animationPinkyX = false, animationThumbY = false;
  private boolean animationIndexZ = false, animationMiddleZ = false, animationRingZ = false, animationPinkyZ = false, animationThumbZ = false;
  private boolean animationPalmZ = false;
  private double savedTime = 0;
   
  public void startAnimation() {
    animation = true;
    startTime = getSeconds()-savedTime;
  }
   
  public void stopAnimation() {
    animation = false;
    double elapsedTime = getSeconds()-startTime;
    savedTime = elapsedTime;
  }
   
  public void incXPosition() {
    xPosition += 0.5f;
    if (xPosition>5f) xPosition = 5f;
    updateMove();
  }
   
  public void decXPosition() {
    xPosition -= 0.5f;
    if (xPosition<-5f) xPosition = -5f;
    updateMove();
  }
 
  private void updateMove() {
    handMoveTranslate.setTransform(Mat4Transform.translate(xPosition,0,0));
    handMoveTranslate.update();
  }
  
  public void rotateWrist() {
    stopAnimation();
    yPosition += 10;
    wristRotation.setTransform(Mat4Transform.rotateAroundY(yPosition));
    wristRotation.update();
  }
   
  public void rotatePalmZ() {
    animationPalmZ = true;
    if (palmZ >= 180) palmZ = 0;    
  }

  public void rotateIndexX() {
    animationIndexX = true;
    if (indexX >= 180) indexX = 0;
  }

  public void rotateIndexZ() {
    animationIndexZ = true;
    if (indexZ <= -40) indexZ = 0;
  }

  public void rotateMiddleX() {
    animationMiddleX = true;
    if (middleX >= 180) middleX = 0;
  }

  public void rotateMiddleZ() {
    animationMiddleZ = true;
    if (middleZ <= -40) middleZ = 0;
  }

  public void rotateRingX() {
    animationRingX = true;
    if (ringX >= 180) ringX = 0;
  }

  public void rotateRingZ() {
    animationRingZ = true;
    if (ringZ >= 40) ringZ = 0;
  }

  public void rotatePinkyX() {
    animationPinkyX = true;
    if (pinkyX >= 180) pinkyX = 0;
  }

  public void rotatePinkyZ() {
    animationPinkyZ = true;
    if (pinkyZ >= 40) pinkyZ = 0;
  }
  
  public void rotateThumbY() {
    animationThumbY = true;
    if (thumbY <= -180) thumbY = 0;
  }

  public void rotateThumbZ() {
    animationThumbZ= true;
    if (thumbZ >= 180) thumbZ = 0;
  }

  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Camera camera;
  private Mat4 perspective;
  private Mesh floor, sphere, cube, cube2;
  private Light light;
  private SGNode hand;
  
  private float xPosition = 0;
  private int yPosition = 0;
  private int zPosition = 0, fingerX = 0;
  private int indexX = 0, middleX = 0, ringX = 0, pinkyX = 0, thumbY = 0, thumbZ = 0;
  private int indexZ = 0, middleZ = 0, ringZ = 0, pinkyZ = 0, palmZ = 0;

  private TransformNode translateX, handMoveTranslate, wristRotation, palmRotateZ;
  private TransformNode indexProxRotateX, indexMiddleRotate, indexDisRotate, indexProxRotateZ;
  private TransformNode middleProxRotateX, middleMiddleRotate, middleDisRotate, middleProxRotateZ;
  private TransformNode ringProxRotateX, ringMiddleRotate, ringDisRotate, ringProxRotateZ;
  private TransformNode pinkyProxRotateX, pinkyMiddleRotate, pinkyDisRotate, pinkyProxRotateZ;
  private TransformNode thumbProxRotateY, thumbMiddleRotate, thumbDisRotate, thumbProxRotateZ;
  
  private void initialise(GL3 gl) {
    createRandomNumbers();
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/chequerboard.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/jade_specular.jpg");
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/wattBook.jpg");
    int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/wattBook_specular.jpg");
    
    // make meshes
    floor = new TwoTriangles(gl, textureId0);
    floor.setModelMatrix(Mat4Transform.scale(16,1,16));   
    sphere = new Sphere(gl, textureId1, textureId2);
    cube = new Cube(gl, textureId3, textureId4);
    cube2 = new Cube(gl, textureId5, textureId6);

    light = new Light(gl);
    light.setCamera(camera);
    
    floor.setLight(light);
    floor.setCamera(camera);
    sphere.setLight(light);
    sphere.setCamera(camera);
    cube.setLight(light);
    cube.setCamera(camera);  
    cube2.setLight(light);
    cube2.setCamera(camera);  
    

    MeshNode wristShape = new MeshNode("Cube(wrist)", cube);
    MeshNode palmShape = new MeshNode("Cube(palm)", cube);
    MeshNode pinkyProxShape = new MeshNode("Cube(pinky proximal)", cube);
    MeshNode pinkyMiddleShape = new MeshNode("Cube(pinky middle)", cube);
    MeshNode pinkyDisShape = new MeshNode("Cube(pinky distal)", cube);
    MeshNode ringProxShape = new MeshNode("Cube(ring proximal)", cube);
    MeshNode ringMiddleShape = new MeshNode("Cube(ring middle)", cube);
    MeshNode ringDisShape = new MeshNode("Cube(ring distal)", cube);
    MeshNode middleProxShape = new MeshNode("Cube(middle proximal)", cube);
    MeshNode middleMiddleShape = new MeshNode("Cube(middle middle)", cube);
    MeshNode middleDisShape = new MeshNode("Cube(middle distal)", cube);
    MeshNode indexProxShape = new MeshNode("Cube(index proximal)", cube);
    MeshNode indexMiddleShape = new MeshNode("Cube(index middle)", cube);
    MeshNode indexDisShape = new MeshNode("Cube(index distal)", cube);
    MeshNode thumbProxShape = new MeshNode("Cube(thumb proximal)", cube);
    MeshNode thumbMiddleShape = new MeshNode("Cube(thumb middle)", cube);
    MeshNode thumbDisShape = new MeshNode("Cube(thumb distal)", cube);
    

    hand = new NameNode("root");
    NameNode wrist = new NameNode("wrist");
    NameNode palm = new NameNode("palm");
    NameNode pinkyProx = new NameNode("pinky proximal");
    NameNode pinkyMiddle = new NameNode("pinky middle");
    NameNode pinkyDis = new NameNode("pinky distal");
    NameNode ringProx = new NameNode("ring proximal");
    NameNode ringMiddle = new NameNode("ring middle");
    NameNode ringDis = new NameNode("ring distal");
    NameNode middleProx = new NameNode("middle proximal");
    NameNode middleMiddle = new NameNode("middle middle");
    NameNode middleDis = new NameNode("middle distal");
    NameNode indexProx = new NameNode("index proximal");
    NameNode indexMiddle = new NameNode("index middle");
    NameNode indexDis = new NameNode("index distal");
    NameNode thumbProx = new NameNode("thumb proximal");
    NameNode thumbMiddle = new NameNode("thumb middle");
    NameNode thumbDis = new NameNode("thumb distal");


    float wristHeight = 3.5f;
    float wristWidth = 2f;
    float wristDepth = 0.5f;

    float palmHeight = 3f;
    float palmWidth = 3f;
    

    float fingerWidth = 0.5f;
    float fingerDepth = 0.5f;

    float pinkyHeight = 0.6f;
    float ringHeight = 0.7f;
    float middleHeight = 0.9f;
    float indexHeight = 0.7f;

    float positionMiddle = 0.4f;
    float positionOutside = 1.1f;

    float thumbWidth = 0.7f;
    float thumbHeight = 0.5f;
    float thumbDepth = 0.5f;

    float thumbPositionX = 1.9f;
    float thumbPositionY = 0.5f;
    
    // wrist
    Mat4 m = Mat4Transform.scale(wristWidth,wristHeight,wristDepth);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode wristTransform = new TransformNode("wrist transform", m);
    
    wristRotation = new TransformNode("wrist rotate",Mat4Transform.rotateAroundY(yPosition));

    // palm
    TransformNode palmTranslate = new TransformNode("palm translate", Mat4Transform.translate(0,wristHeight,0));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(palmWidth,palmHeight,wristDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode palmTransform = new TransformNode("palm transform", m);
    
    palmRotateZ = new TransformNode("rotate palm",Mat4Transform.rotateAroundZ(0));

    // pinky proximal
    TransformNode pinkyProxTranslate = new TransformNode("pinky proximal translate", Mat4Transform.translate(-positionOutside, palmHeight, 0));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,pinkyHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode pinkyProxTransform = new TransformNode("pinky proximal transform", m);

    pinkyProxRotateX = new TransformNode("pinky proximal rotate x",Mat4Transform.rotateAroundX(0));
    pinkyProxRotateZ = new TransformNode("pinky proximal rotate z",Mat4Transform.rotateAroundZ(0));

    // pinky middle
    TransformNode pinkyMiddleTranslate = new TransformNode("pinky middle translate", Mat4Transform.translate(0, pinkyHeight, 0));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,pinkyHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode pinkyMiddleTransform = new TransformNode("pinky middle transform", m);

    pinkyMiddleRotate = new TransformNode("pinky middle rotate",Mat4Transform.rotateAroundX(0));

    // pinky distal
    TransformNode pinkyDisTranslate = new TransformNode("pinky distal translate", Mat4Transform.translate(0, pinkyHeight, 0));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,pinkyHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode pinkyDisTransform = new TransformNode("pinky distal transform", m);

    pinkyDisRotate = new TransformNode("pinky distal rotate",Mat4Transform.rotateAroundX(0));

    // ring proximal
    TransformNode ringProxTranslate = new TransformNode("ring proximal translate", Mat4Transform.translate(-positionMiddle, palmHeight, 0));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,ringHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode ringProxTransform = new TransformNode("ring proximal transform", m);

    ringProxRotateX = new TransformNode("ring proximal rotate x",Mat4Transform.rotateAroundX(0));
    ringProxRotateZ = new TransformNode("ring proximal rotate z",Mat4Transform.rotateAroundZ(0));

    // ring middle
    TransformNode ringMiddleTranslate = new TransformNode("ring middle translate", Mat4Transform.translate(0, ringHeight, 0));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,ringHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode ringMiddleTransform = new TransformNode("ring middle transform", m);

    ringMiddleRotate = new TransformNode("ring middle rotate",Mat4Transform.rotateAroundX(0));

    // ring distal
    TransformNode ringDisTranslate = new TransformNode("ring distal translate", Mat4Transform.translate(0, ringHeight, 0));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,ringHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode ringDisTransform = new TransformNode("ring distal transform", m);

    ringDisRotate = new TransformNode("ring distal rotate",Mat4Transform.rotateAroundX(0));

    // middle proximal
    TransformNode middleProxTranslate = new TransformNode("middle proximal translate", Mat4Transform.translate(positionMiddle, palmHeight, 0));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,middleHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode middleProxTransform = new TransformNode("middle proximal transform", m);

    middleProxRotateX = new TransformNode("middle proximal rotate x",Mat4Transform.rotateAroundX(0));
    middleProxRotateZ = new TransformNode("middle proximal rotate z",Mat4Transform.rotateAroundZ(0));

    // middle middle
    TransformNode middleMiddleTranslate = new TransformNode("middle middle translate", Mat4Transform.translate(0, middleHeight, 0));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,middleHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode middleMiddleTransform = new TransformNode("middle middle transform", m);

    middleMiddleRotate = new TransformNode("middle middle rotate",Mat4Transform.rotateAroundX(0));

    // middle distal
    TransformNode middleDisTranslate = new TransformNode("middle distal translate", Mat4Transform.translate(0, middleHeight, 0));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,middleHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode middleDisTransform = new TransformNode("middle distal transform", m);

    middleDisRotate = new TransformNode("middle distal rotate",Mat4Transform.rotateAroundX(0));

    // index proximal
    TransformNode indexProxTranslate = new TransformNode("index proximal translate", Mat4Transform.translate(positionOutside, palmHeight, 0));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,indexHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode indexProxTransform = new TransformNode("index proximal transform", m);

    indexProxRotateX = new TransformNode("index proximal rotate x",Mat4Transform.rotateAroundX(0));
    indexProxRotateZ = new TransformNode("index proximal rotate z",Mat4Transform.rotateAroundZ(0));

    // index middle
    TransformNode indexMiddleTranslate = new TransformNode("index middle translate", Mat4Transform.translate(0, indexHeight, 0));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,indexHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode indexMiddleTransform = new TransformNode("index middle transform", m);

    indexMiddleRotate = new TransformNode("index middle rotate",Mat4Transform.rotateAroundX(0));

    // index distal
    TransformNode indexDisTranslate = new TransformNode("index distal translate", Mat4Transform.translate(0, indexHeight, 0));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,indexHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode indexDisTransform = new TransformNode("index distal transform", m);

    indexDisRotate = new TransformNode("index distal rotate",Mat4Transform.rotateAroundX(0));

    // thumb proximal
    TransformNode thumbProxTranslate = new TransformNode("thumb proximal translate", Mat4Transform.translate(thumbPositionX, thumbPositionY, 0));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(thumbWidth,thumbHeight,thumbDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode thumbProxTransform = new TransformNode("thumb proximal transform", m);

    thumbProxRotateY = new TransformNode("thumb proximal rotate y",Mat4Transform.rotateAroundY(0));
    thumbProxRotateZ = new TransformNode("thumb proximal rotate z",Mat4Transform.rotateAroundZ(0));

    // thumb middle
    TransformNode thumbMiddleTranslate = new TransformNode("thumb middle translate", Mat4Transform.translate(thumbWidth, 0, 0));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(thumbWidth,thumbHeight,thumbDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode thumbMiddleTransform = new TransformNode("thumb middle transform", m);

    thumbMiddleRotate = new TransformNode("thumb middle rotate",Mat4Transform.rotateAroundY(0));

    // thumb distal
    TransformNode thumbDisTranslate = new TransformNode("thumb distal translate", Mat4Transform.translate(thumbWidth, 0, 0));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(thumbWidth,thumbHeight,thumbDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode thumbDisTransform = new TransformNode("thumb distal transform", m);

    thumbDisRotate = new TransformNode("thumb distal rotate",Mat4Transform.rotateAroundY(0));


    hand.addChild(wrist);
      wrist.addChild(wristRotation);
        wristRotation.addChild(wristTransform);
          wristTransform.addChild(wristShape);
            wristRotation.addChild(palm);

            palm.addChild(palmTranslate);
              palmTranslate.addChild(palmRotateZ);
                palmRotateZ.addChild(palmTransform);
                  palmTransform.addChild(palmShape);

              palmRotateZ.addChild(pinkyProxTranslate);
                pinkyProxTranslate.addChild(pinkyProx);
                  pinkyProx.addChild(pinkyProxRotateX);
                    pinkyProxRotateX.addChild(pinkyProxRotateZ);
                      pinkyProxRotateZ.addChild(pinkyProxTransform);
                        pinkyProxTransform.addChild(pinkyProxShape); // pinky proximal
                      pinkyProxRotateZ.addChild(pinkyMiddleTranslate);
                        pinkyMiddleTranslate.addChild(pinkyMiddle);
                          pinkyMiddle.addChild(pinkyMiddleRotate);
                            pinkyMiddleRotate.addChild(pinkyMiddleTransform);
                              pinkyMiddleTransform.addChild(pinkyMiddleShape); // pinky middle
                            pinkyMiddleRotate.addChild(pinkyDisTranslate);
                              pinkyDisTranslate.addChild(pinkyDis);
                                pinkyDis.addChild(pinkyDisRotate);
                                  pinkyDisRotate.addChild(pinkyDisTransform); // pinky distal
                                    pinkyDisTransform.addChild(pinkyDisShape);

              palmRotateZ.addChild(ringProxTranslate);
                ringProxTranslate.addChild(ringProx);
                  ringProx.addChild(ringProxRotateX);
                    ringProxRotateX.addChild(ringProxRotateZ);
                      ringProxRotateZ.addChild(ringProxTransform);
                        ringProxTransform.addChild(ringProxShape); // ring proximal
                      ringProxRotateZ.addChild(ringMiddleTranslate);
                        ringMiddleTranslate.addChild(ringMiddle);
                          ringMiddle.addChild(ringMiddleRotate);
                            ringMiddleRotate.addChild(ringMiddleTransform);
                              ringMiddleTransform.addChild(ringMiddleShape); // ring middle
                            ringMiddleRotate.addChild(ringDisTranslate);
                              ringDisTranslate.addChild(ringDis);
                                ringDis.addChild(ringDisRotate);
                                  ringDisRotate.addChild(ringDisTransform);
                                    ringDisTransform.addChild(ringDisShape); // ring distal

              palmRotateZ.addChild(middleProxTranslate);
                middleProxTranslate.addChild(middleProx);
                  middleProx.addChild(middleProxRotateX);
                    middleProxRotateX.addChild(middleProxRotateZ);
                      middleProxRotateZ.addChild(middleProxTransform);
                        middleProxTransform.addChild(middleProxShape); // middle proximal
                      middleProxRotateZ.addChild(middleMiddleTranslate);
                        middleMiddleTranslate.addChild(middleMiddle);
                          middleMiddle.addChild(middleMiddleRotate);
                            middleMiddleRotate.addChild(middleMiddleTransform);
                              middleMiddleTransform.addChild(middleMiddleShape); // middle middle
                            middleMiddleRotate.addChild(middleDisTranslate);
                              middleDisTranslate.addChild(middleDis);
                                middleDis.addChild(middleDisRotate);
                                  middleDisRotate.addChild(middleDisTransform);
                                    middleDisTransform.addChild(middleDisShape); // middle distal

              palmRotateZ.addChild(indexProxTranslate);
                indexProxTranslate.addChild(indexProx);
                  indexProx.addChild(indexProxRotateX);
                    indexProxRotateX.addChild(indexProxRotateZ);
                      indexProxRotateZ.addChild(indexProxTransform);
                        indexProxTransform.addChild(indexProxShape); // index proximal
                      indexProxRotateZ.addChild(indexMiddleTranslate);
                        indexMiddleTranslate.addChild(indexMiddle);
                          indexMiddle.addChild(indexMiddleRotate);
                            indexMiddleRotate.addChild(indexMiddleTransform);
                              indexMiddleTransform.addChild(indexMiddleShape); // index middle
                            indexMiddleRotate.addChild(indexDisTranslate);
                              indexDisTranslate.addChild(indexDis);
                                indexDis.addChild(indexDisRotate);
                                  indexDisRotate.addChild(indexDisTransform);
                                    indexDisTransform.addChild(indexDisShape); // index distal

              palmRotateZ.addChild(thumbProxTranslate);
                thumbProxTranslate.addChild(thumbProx);
                  thumbProx.addChild(thumbProxRotateY);
                    thumbProxRotateY.addChild(thumbProxRotateZ);
                      thumbProxRotateZ.addChild(thumbProxTransform);
                        thumbProxTransform.addChild(thumbProxShape); // thumb proximal
                      thumbProxRotateZ.addChild(thumbMiddleTranslate);
                        thumbMiddleTranslate.addChild(thumbMiddle);
                          thumbMiddle.addChild(thumbMiddleRotate);
                            thumbMiddleRotate.addChild(thumbMiddleTransform);
                              thumbMiddleTransform.addChild(thumbMiddleShape); // thumb middle
                            thumbMiddleRotate.addChild(thumbDisTranslate);
                              thumbDisTranslate.addChild(thumbDis);
                                thumbDis.addChild(thumbDisRotate);
                                  thumbDisRotate.addChild(thumbDisTransform);
                                    thumbDisTransform.addChild(thumbDisShape); // thumb distal


    hand.update();
  }

  private int delayTime = 0, palmZcount = 0;
  private int indexXcount = 0, middleXcount = 0, ringXcount = 0, pinkyXcount = 0;
  private int indexZcount = 0, middleZcount = 0, ringZcount = 0, pinkyZcount = 0;
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    updatePerspectiveMatrices();
    
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);

    floor.render(gl); 
    
    if (animationIndexX) {
      if (indexX <= 90) {
        updateIndexXForward();
        indexXcount++;
      } else {
        if (indexXcount >= 200) {
          updateIndexXBackward();
          if (indexX >= 180) indexXcount = 0;
        } else {
          indexXcount++;
        }
      }
    }

    if (animationMiddleX) {
      if (middleX <= 90) {
        updateMiddleXForward();
        middleXcount++;
      } else {
        if (middleXcount >= 200) {
          updateMiddleXBackward();
          if (middleX >= 180) middleXcount = 0;
        } else {
          middleXcount++;
        }
      }
    }

    if (animationRingX) updateRingX();
    if (animationPinkyX) updatePinkyX();
    if (animationThumbY) updateThumbY();

    if (animationPalmZ) {
      if (palmZ <= 90) {
        updatePalmZForward();
        palmZcount++;
      } else {
        if (palmZcount >= 200) {
          updatePalmZBackward();
          if (palmZ >= 180) palmZcount = 0;
        } else {
          palmZcount++;
        }
      }
    }



    if (animationIndexZ) updateIndexZ();
    if (animationMiddleZ) updateMiddleZ();
    if (animationRingZ) updateRingZ();
    if (animationPinkyZ) updatePinkyZ();
    if (animationThumbZ) updateThumbZ();
    hand.draw(gl);
  }
    
  private void updatePerspectiveMatrices() {
    // needs to be changed if user resizes the window
    perspective = Mat4Transform.perspective(45, aspect);
    light.setPerspective(perspective);
    floor.setPerspective(perspective);
    sphere.setPerspective(perspective);
    cube.setPerspective(perspective);
    cube2.setPerspective(perspective);
  }
  
  private void disposeMeshes(GL3 gl) {
    light.dispose(gl);
    floor.dispose(gl);
    sphere.dispose(gl);
    cube.dispose(gl);
    cube2.dispose(gl);
  }

  private void updatePalmZForward() {
    palmZ += 1;
    if (palmZ <= 90) {
      palmRotateZ.setTransform(Mat4Transform.rotateAroundZ(palmZ));
      palmRotateZ.update();
    }
  }

  private void updatePalmZBackward() {
  palmZ += 1;
    if (palmZ > 90 && palmZ <= 180) {
      int i = palmZ - 90;
      int palmPos = 90 - i;
      palmRotateZ.setTransform(Mat4Transform.rotateAroundZ(palmPos));
      palmRotateZ.update();
    }
  }
  
  private void updateIndexXForward() {
    indexX += 1;
    if (indexX <= 90) {
      indexProxRotateX.setTransform(Mat4Transform.rotateAroundX(indexX));
      indexProxRotateX.update();
      indexMiddleRotate.setTransform(Mat4Transform.rotateAroundX(indexX));
      indexMiddleRotate.update();
      indexDisRotate.setTransform(Mat4Transform.rotateAroundX(indexX));
      indexDisRotate.update();
    }
  }

  private void updateIndexXBackward() {
    indexX += 1;
    if (indexX > 90 && indexX <= 180){
      int i = indexX - 90;
      int indexPos = 90 - i;
      indexProxRotateX.setTransform(Mat4Transform.rotateAroundX(indexPos));
      indexProxRotateX.update();
      indexMiddleRotate.setTransform(Mat4Transform.rotateAroundX(indexPos));
      indexMiddleRotate.update();
      indexDisRotate.setTransform(Mat4Transform.rotateAroundX(indexPos));
      indexDisRotate.update();
    }
  }

  private void updateIndexZ() {
    indexZ -= 1;
    if (indexZ >= -20) {
      indexProxRotateZ.setTransform(Mat4Transform.rotateAroundZ(indexZ));
      indexProxRotateZ.update();
    } else if (indexZ < -20 && indexZ >= -40){
      int i = indexZ + 20;
      int indexPos = -20 + (-i);
      indexProxRotateZ.setTransform(Mat4Transform.rotateAroundZ(indexPos));
      indexProxRotateZ.update();
    }
  }

  private void updateMiddleXForward() {
    middleX += 1;
    if (middleX <= 90) {
      middleProxRotateX.setTransform(Mat4Transform.rotateAroundX(middleX));
      middleProxRotateX.update();
      middleMiddleRotate.setTransform(Mat4Transform.rotateAroundX(middleX));
      middleMiddleRotate.update();
      middleDisRotate.setTransform(Mat4Transform.rotateAroundX(middleX));
      middleDisRotate.update();
    }
  }

  private void updateMiddleXBackward() {
    middleX += 1;
    if (middleX > 90 && middleX <= 180){
      int i = middleX - 90;
      int middlePos = 90 - i;
      middleProxRotateX.setTransform(Mat4Transform.rotateAroundX(middlePos));
      middleProxRotateX.update();
      middleMiddleRotate.setTransform(Mat4Transform.rotateAroundX(middlePos));
      middleMiddleRotate.update();
      middleDisRotate.setTransform(Mat4Transform.rotateAroundX(middlePos));
      middleDisRotate.update();
    }
  }

  private void updateMiddleZ() {
    middleZ -= 1;
    if (middleZ >= -20) {
      middleProxRotateZ.setTransform(Mat4Transform.rotateAroundZ(middleZ));
      middleProxRotateZ.update();
    } else if (middleZ < -20 && middleZ >= -40){
      int i = middleZ + 20;
      int middlePos = -20 + (-i);
      middleProxRotateZ.setTransform(Mat4Transform.rotateAroundZ(middlePos));
      middleProxRotateZ.update();
    }
  }

  private void updateRingX() {
    ringX += 1;
    if (ringX <= 90) {
      ringProxRotateX.setTransform(Mat4Transform.rotateAroundX(ringX));
      ringProxRotateX.update();
      ringMiddleRotate.setTransform(Mat4Transform.rotateAroundX(ringX));
      ringMiddleRotate.update();
      ringDisRotate.setTransform(Mat4Transform.rotateAroundX(ringX));
      ringDisRotate.update();
    } else if (ringX > 90 && ringX <= 180){
      int i = ringX - 90;
      int ringPos = 90 - i;
      ringProxRotateX.setTransform(Mat4Transform.rotateAroundX(ringPos));
      ringProxRotateX.update();
      ringMiddleRotate.setTransform(Mat4Transform.rotateAroundX(ringPos));
      ringMiddleRotate.update();
      ringDisRotate.setTransform(Mat4Transform.rotateAroundX(ringPos));
      ringDisRotate.update();
    }
  }

  private void updateRingZ() {
    ringZ += 1;
    if (ringZ <= 20) {
      ringProxRotateZ.setTransform(Mat4Transform.rotateAroundZ(ringZ));
      ringProxRotateZ.update();
    } else if (ringZ > 20 && ringZ <= 40) {
      int i = ringZ - 20;
      int ringPos = 20 - i;
      ringProxRotateZ.setTransform(Mat4Transform.rotateAroundZ(ringPos));
      ringProxRotateZ.update();
    }
  }

  private void updatePinkyX() {
    pinkyX += 1;
    if (pinkyX <= 90) {
      pinkyProxRotateX.setTransform(Mat4Transform.rotateAroundX(pinkyX));
      pinkyProxRotateX.update();
      pinkyMiddleRotate.setTransform(Mat4Transform.rotateAroundX(pinkyX));
      pinkyMiddleRotate.update();
      pinkyDisRotate.setTransform(Mat4Transform.rotateAroundX(pinkyX));
      pinkyDisRotate.update();
    } else if (pinkyX > 90 && pinkyX <= 180){
      int i = pinkyX - 90;
      int pinkyPos = 90 - i;
      pinkyProxRotateX.setTransform(Mat4Transform.rotateAroundX(pinkyPos));
      pinkyProxRotateX.update();
      pinkyMiddleRotate.setTransform(Mat4Transform.rotateAroundX(pinkyPos));
      pinkyMiddleRotate.update();
      pinkyDisRotate.setTransform(Mat4Transform.rotateAroundX(pinkyPos));
      pinkyDisRotate.update();
    }
  }

  private void updatePinkyZ() {
    pinkyZ += 1;
    if (pinkyZ <= 20) {
      pinkyProxRotateZ.setTransform(Mat4Transform.rotateAroundZ(pinkyZ));
      pinkyProxRotateZ.update();
    } else if (pinkyZ > 20 && pinkyZ <= 40) {
      int i = pinkyZ - 20;
      int pinkyPos = 20 - i;
      pinkyProxRotateZ.setTransform(Mat4Transform.rotateAroundZ(pinkyPos));
      pinkyProxRotateZ.update();
    }
  }

  private void updateThumbY() {
    thumbY -= 1;
    if (thumbY >= -90) {
      thumbProxRotateY.setTransform(Mat4Transform.rotateAroundY(thumbY));
      thumbProxRotateY.update();
      thumbMiddleRotate.setTransform(Mat4Transform.rotateAroundY(thumbY));
      thumbMiddleRotate.update();
      thumbDisRotate.setTransform(Mat4Transform.rotateAroundY(thumbY));
      thumbDisRotate.update();
    } else if (thumbY < -90 && thumbY >= -180){
      int i = thumbY + 90;
      int thumbPos = -90 + (-i);
      thumbProxRotateY.setTransform(Mat4Transform.rotateAroundY(thumbPos));
      thumbProxRotateY.update();
      thumbMiddleRotate.setTransform(Mat4Transform.rotateAroundY(thumbPos));
      thumbMiddleRotate.update();
      thumbDisRotate.setTransform(Mat4Transform.rotateAroundY(thumbPos));
      thumbDisRotate.update();
    }
  }

  private void updateThumbZ() {
    thumbZ += 1;
    if (thumbZ <= 90) {
      thumbProxRotateZ.setTransform(Mat4Transform.rotateAroundZ(thumbZ));
      thumbProxRotateZ.update();
    } else if (thumbZ > 90 && thumbZ <= 180) {
      int i = thumbZ - 90;
      int thumbPos = 90 - i;
      thumbProxRotateZ.setTransform(Mat4Transform.rotateAroundZ(thumbPos));
      thumbProxRotateZ.update();
    }
  }
  
  // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);   
    // return new Vec3(5f,3.4f,5f);
  }
  
}