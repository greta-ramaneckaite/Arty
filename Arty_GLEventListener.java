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
  
  public void loweredArms() {
    stopAnimation();
    // leftArmRotate.setTransform(Mat4Transform.rotateAroundX(180));
    // leftArmRotate.update();
    // rightArmRotate.setTransform(Mat4Transform.rotateAroundX(180));
    // rightArmRotate.update();    
  }
   
  public void raisedArms() {
    stopAnimation();
    // leftArmRotate.setTransform(Mat4Transform.rotateAroundX(0));
    // leftArmRotate.update();
    // rightArmRotate.setTransform(Mat4Transform.rotateAroundX(0));
    // rightArmRotate.update();    
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
  private TransformNode translateX, handMoveTranslate; //rotations go here
  
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
    
    // make nodes
    // MeshNode bodyShape = new MeshNode("Cube(body)", cube);
    // MeshNode headShape = new MeshNode("Sphere(head)", sphere);
    // MeshNode leftArmShape = new MeshNode("Cube(left arm)", cube2);
    // MeshNode rightArmShape = new MeshNode("Cube(right arm)", cube2);
    // MeshNode leftLegShape = new MeshNode("Cube(leftleg)", cube);
    // MeshNode rightLegShape = new MeshNode("Cube(rightleg)", cube);

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
    
    // robot = new NameNode("root");
    // NameNode body = new NameNode("body");
    // NameNode head = new NameNode("head");
    // NameNode leftarm = new NameNode("left arm");
    // NameNode rightarm = new NameNode("right arm");
    // NameNode leftleg = new NameNode("left leg");
    // NameNode rightleg = new NameNode("right leg");

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

    
    // float bodyHeight = 3f;
    // float bodyWidth = 2f;
    // float bodyDepth = 1f;
    // float headScale = 2f;
    // float armLength = 3.5f;
    // float armScale = 0.5f;
    // float legLength = 3.5f;
    // float legScale = 0.67f;

    float wristHeight = 3.5f;
    float wristWidth = 1.5f;
    float wristDepth = 0.5f;
    float palmHeight = 3f;
    float palmWidth = 3f;
    

    float fingerWidth = 0.5f;
    float fingerDepth = 0.5f;

    float pinkyProxHeight = 1f;
    float pinkyMiddleHeight = 1f;
    float pinkyDisHeight = 1f;

    float ringProxHeight = 1.1f;
    float ringMiddleHeight = 1.1f;
    float ringDisHeight = 1.1f;

    float middleProxHeight = 1.3f;
    float middleMiddleHeight = 1.3f;
    float middleDisHeight = 1.3f;

    float indexProxHeight = 1.1f;
    float indexMiddleHeight = 1.1f;
    float indexDisHeight = 1.1f;

    float thumbWidth = 1f;
    float thumbHeight = 0.5f;
    float thumbDepth = 0.5f;


    // robotMoveTranslate = new TransformNode("robot transform",Mat4Transform.translate(xPosition,0,0));
    // TransformNode robotTranslate = new TransformNode("robot transform",Mat4Transform.translate(0,legLength,0));

    handMoveTranslate = new TransformNode("hand transform",Mat4Transform.translate(xPosition,0,0));
    TransformNode handTranslate = new TransformNode("hand transform",Mat4Transform.translate(0,0,0));
    
    Mat4 m = Mat4Transform.scale(wristWidth,wristHeight,wristDepth);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode wristTransform = new TransformNode("wrist transform", m);

    // palm
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0,wristHeight,0));
    m = Mat4.multiply(m, Mat4Transform.scale(palmWidth,palmHeight,wristDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode palmTransform = new TransformNode("palm transform", m);

    // pinky proximal
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(-(palmWidth / 2),palmHeight + wristHeight,0));
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,pinkyProxHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode pinkyProxTransform = new TransformNode("pinky proximal transform", m);

    // pinky middle
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(-(palmWidth / 2),palmHeight + wristHeight + pinkyProxHeight,0));
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,pinkyMiddleHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode pinkyMiddleTransform = new TransformNode("pinky middle transform", m);

    // pinky distal
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(-(palmWidth / 2),palmHeight + wristHeight + pinkyProxHeight + pinkyMiddleHeight,0));
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,pinkyDisHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode pinkyDisTransform = new TransformNode("pinky distal transform", m);

    // ring proximal
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(-(palmWidth / 5),palmHeight + wristHeight,0));
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,ringProxHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode ringProxTransform = new TransformNode("ring proximal transform", m);

    // ring middle
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(-(palmWidth / 5),palmHeight + wristHeight + ringMiddleHeight,0));
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,ringMiddleHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode ringMiddleTransform = new TransformNode("ring middle transform", m);

    // ring distal
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(-(palmWidth / 5),palmHeight + wristHeight + ringProxHeight + ringMiddleHeight,0));
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,ringDisHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode ringDisTransform = new TransformNode("ring distal transform", m);

    // middle proximal
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate((palmWidth / 5),palmHeight + wristHeight,0));
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,middleProxHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode middleProxTransform = new TransformNode("middle proximal transform", m);

    // middle middle
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate((palmWidth / 5),palmHeight + wristHeight + middleMiddleHeight,0));
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,middleMiddleHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode middleMiddleTransform = new TransformNode("middle middle transform", m);

    // middle distal
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate((palmWidth / 5),palmHeight + wristHeight + middleProxHeight + middleMiddleHeight,0));
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,middleDisHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode middleDisTransform = new TransformNode("middle distal transform", m);

    // index proximal
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate((palmWidth / 2),palmHeight + wristHeight,0));
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,indexProxHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode indexProxTransform = new TransformNode("index proximal transform", m);

    // index middle
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate((palmWidth / 2),palmHeight + wristHeight + indexMiddleHeight,0));
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,indexMiddleHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode indexMiddleTransform = new TransformNode("index middle transform", m);

    // index distal
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate((palmWidth / 2),palmHeight + wristHeight + indexProxHeight + indexMiddleHeight,0));
    m = Mat4.multiply(m, Mat4Transform.scale(fingerWidth,indexDisHeight,fingerDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode indexDisTransform = new TransformNode("index distal transform", m);

    // thumb proximal
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(palmWidth-thumbWidth,palmHeight+thumbHeight,0));
    m = Mat4.multiply(m, Mat4Transform.scale(thumbWidth,thumbHeight,thumbDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode thumbProxTransform = new TransformNode("thumb proximal transform", m);

    // thumb middle
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(palmWidth,palmHeight+thumbHeight,0));
    m = Mat4.multiply(m, Mat4Transform.scale(thumbWidth,thumbHeight,thumbDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode thumbMiddleTransform = new TransformNode("thumb middle transform", m);

    // thumb distal
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(palmWidth+thumbWidth,palmHeight+thumbHeight,0));
    m = Mat4.multiply(m, Mat4Transform.scale(thumbWidth,thumbHeight,thumbDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode thumbDisTransform = new TransformNode("thumb distal transform", m);

    // m = new Mat4(1);   
    // TransformNode leftArmTranslate = new TransformNode("leftarm translate", 
    //                                        Mat4Transform.translate((bodyWidth*0.5f)+(armScale*0.5f),bodyHeight,0));
    // leftArmRotate = new TransformNode("leftarm rotate",Mat4Transform.rotateAroundX(180));
    // m = new Mat4(1);
    // m = Mat4.multiply(m, Mat4Transform.scale(armScale,armLength,armScale));
    // m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    // TransformNode leftArmScale = new TransformNode("leftarm scale", m);
    
    // TransformNode rightArmTranslate = new TransformNode("rightarm translate", 
    //                                       Mat4Transform.translate(-(bodyWidth*0.5f)-(armScale*0.5f),bodyHeight,0));
    // rightArmRotate = new TransformNode("rightarm rotate",Mat4Transform.rotateAroundX(180));
    // m = new Mat4(1);
    // m = Mat4.multiply(m, Mat4Transform.scale(armScale,armLength,armScale));
    // m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    // TransformNode rightArmScale = new TransformNode("rightarm scale", m);
    
    // m = new Mat4(1);
    // m = Mat4.multiply(m, Mat4Transform.translate((bodyWidth*0.5f)-(legScale*0.5f),0,0));
    // m = Mat4.multiply(m, Mat4Transform.rotateAroundX(180));
    // m = Mat4.multiply(m, Mat4Transform.scale(legScale,legLength,legScale));
    // m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    // TransformNode leftlegTransform = new TransformNode("leftleg transform", m);

    // m = new Mat4(1);
    // m = Mat4.multiply(m, Mat4Transform.translate(-(bodyWidth*0.5f)+(legScale*0.5f),0,0));
    // m = Mat4.multiply(m, Mat4Transform.rotateAroundX(180));
    // m = Mat4.multiply(m, Mat4Transform.scale(legScale,legLength,legScale));
    // m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    // TransformNode rightlegTransform = new TransformNode("rightleg transform", m);
        
    // 
    // make scene graph
    // robot.addChild(robotMoveTranslate);
    //   robotMoveTranslate.addChild(robotTranslate);
    //     robotTranslate.addChild(body);
    //       body.addChild(bodyTransform);
    //         bodyTransform.addChild(bodyShape);
    //       body.addChild(head);
    //         head.addChild(headTransform);
    //         headTransform.addChild(headShape);
    //       body.addChild(leftarm);
    //         leftarm.addChild(leftArmTranslate);
    //         leftArmTranslate.addChild(leftArmRotate);
    //         leftArmRotate.addChild(leftArmScale);
    //         leftArmScale.addChild(leftArmShape);
    //       body.addChild(rightarm);
    //         rightarm.addChild(rightArmTranslate);
    //         rightArmTranslate.addChild(rightArmRotate);
    //         rightArmRotate.addChild(rightArmScale);
    //         rightArmScale.addChild(rightArmShape);
    //       body.addChild(leftleg);
    //         leftleg.addChild(leftlegTransform);
    //         leftlegTransform.addChild(leftLegShape);
    //       body.addChild(rightleg);
    //         rightleg.addChild(rightlegTransform);
    //         rightlegTransform.addChild(rightLegShape);
    
    // robot.update();  // IMPORTANT - don't forget this

    hand.addChild(handMoveTranslate);
      handMoveTranslate.addChild(handTranslate);
        handTranslate.addChild(wrist);
          wrist.addChild(wristTransform);
            wristTransform.addChild(wristShape);
          wrist.addChild(palm);
            palm.addChild(palmTransform);
              palmTransform.addChild(palmShape);
            palm.addChild(pinkyProx);
              pinkyProx.addChild(pinkyProxTransform);
                pinkyProxTransform.addChild(pinkyProxShape); // pinky proximal
                  pinkyProx.addChild(pinkyMiddle);
                    pinkyMiddle.addChild(pinkyMiddleTransform);
                      pinkyMiddleTransform.addChild(pinkyMiddleShape); // pinky middle
                        pinkyMiddle.addChild(pinkyDis);
                          pinkyDis.addChild(pinkyDisTransform);
                            pinkyDisTransform.addChild(pinkyDisShape); // pinky distal
            palm.addChild(ringProx);
              ringProx.addChild(ringProxTransform);
                ringProxTransform.addChild(ringProxShape); // ring proximal
                  ringProx.addChild(ringMiddle);
                    ringMiddle.addChild(ringMiddleTransform);
                      ringMiddleTransform.addChild(ringMiddleShape); // ring middle
                        ringMiddle.addChild(ringDis);
                          ringDis.addChild(ringDisTransform);
                            ringDisTransform.addChild(ringDisShape); // pinky distal
            palm.addChild(middleProx);
              middleProx.addChild(middleProxTransform);
                middleProxTransform.addChild(middleProxShape); // middle proximal
                  middleProx.addChild(middleMiddle);
                    middleMiddle.addChild(middleMiddleTransform);
                      middleMiddleTransform.addChild(middleMiddleShape); // middle middle
                        middleMiddle.addChild(middleDis);
                          middleDis.addChild(middleDisTransform);
                            middleDisTransform.addChild(middleDisShape); // middle distal
            palm.addChild(indexProx);
              indexProx.addChild(indexProxTransform);
                indexProxTransform.addChild(indexProxShape); // index proximal
                  indexProx.addChild(indexMiddle);
                    indexMiddle.addChild(indexMiddleTransform);
                      indexMiddleTransform.addChild(indexMiddleShape); // index middle
                        indexMiddle.addChild(indexDis);
                          indexDis.addChild(indexDisTransform);
                            indexDisTransform.addChild(indexDisShape); // index distal
            palm.addChild(thumbProx);
              thumbProx.addChild(thumbProxTransform);
                thumbProxTransform.addChild(thumbProxShape); // thumb proximal
                  thumbProx.addChild(thumbMiddle);
                    thumbMiddle.addChild(thumbMiddleTransform);
                      thumbMiddleTransform.addChild(thumbMiddleShape); // thumb middle
                        thumbMiddle.addChild(thumbDis);
                          thumbDis.addChild(thumbDisTransform);
                            thumbDisTransform.addChild(thumbDisShape); // thumb distal


    hand.update();
  }
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    updatePerspectiveMatrices();
    
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);

    floor.render(gl); 
    
    // if (animation) updateLeftArm();
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
  
  // private void updateLeftArm() {
  //   double elapsedTime = getSeconds()-startTime;
  //   float rotateAngle = 180f+90f*(float)Math.sin(elapsedTime);
  //   leftArmRotate.setTransform(Mat4Transform.rotateAroundX(rotateAngle));
  //   leftArmRotate.update();
  //   rotateAngle = -rotateAngle;
  //   rightArmRotate.setTransform(Mat4Transform.rotateAroundX(rotateAngle));
  //   rightArmRotate.update();
  // }
  
  // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);   
    //return new Vec3(5f,3.4f,5f);
  }
  
}