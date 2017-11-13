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
  private Mesh cube, tt1, tt2, tt3, tt4, tt5, tt6;
  private Light light;
    
  private Camera camera;
    
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
    cube.dispose(gl);
    tt1.dispose(gl); // dunno
    tt2.dispose(gl); // dunno
    light.dispose(gl);
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
    robotMoveTranslate.setTransform(Mat4Transform.translate(xPosition,0,0));
    robotMoveTranslate.update();
  }
  
  public void loweredArms() {
    stopAnimation();
    leftArmRotate.setTransform(Mat4Transform.rotateAroundX(180));
    leftArmRotate.update();
    rightArmRotate.setTransform(Mat4Transform.rotateAroundX(180));
    rightArmRotate.update();    
  }
   
  public void raisedArms() {
    stopAnimation();
    leftArmRotate.setTransform(Mat4Transform.rotateAroundX(0));
    leftArmRotate.update();
    rightArmRotate.setTransform(Mat4Transform.rotateAroundX(0));
    rightArmRotate.update();    
  }


  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Mat4 perspective;
  private Mesh floor, sphere, cube, cube2;
  private SGNode robot;
  
  private float xPosition = 0;
  private TransformNode translateX, robotMoveTranslate, leftArmRotate, rightArmRotate;
  

  public void initialise(GL3 gl) {
    createRandomNumbers();
    // cube
    int[] textureId0 = TextureLibrary.loadTexture(gl, "container2.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "container2_specular.jpg");

    // floor
    int[] textureId2 = TextureLibrary.loadTexture(gl, "chequerboard.jpg");
    // front wall
    int[] textureId3 = TextureLibrary.loadTexture(gl, "cloud.jpg");
    // right wall
    int[] textureId4 = TextureLibrary.loadTexture(gl, "wall.jpg");
    // back wall
    int[] textureId5 = TextureLibrary.loadTexture(gl, "wall.jpg");
    // left wall
    int[] textureId6 = TextureLibrary.loadTexture(gl, "wall.jpg");
    // ceiling
    int[] textureId7 = TextureLibrary.loadTexture(gl, "wattBook.jpg");

    // make meshes
    floor = new TwoTriangles(gl, textureId0);
    floor.setModelMatrix(Mat4Transform.scale(16,1,16));   
    sphere = new Sphere(gl, textureId1, textureId2);
    cube = new Cube(gl, textureId3, textureId4);
    cube2 = new Cube(gl, textureId5, textureId6);

    cube = new Cube(gl, textureId0, textureId1);
    tt1 = new TwoTriangles(gl, textureId2);
    tt2 = new TwoTriangles(gl, textureId3);
    tt3 = new TwoTriangles(gl, textureId4);
    tt4 = new TwoTriangles(gl, textureId5);
    tt5 = new TwoTriangles(gl, textureId6);
    tt6 = new TwoTriangles(gl, textureId7);

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
    MeshNode bodyShape = new MeshNode("Cube(body)", cube);
    MeshNode headShape = new MeshNode("Sphere(head)", sphere);
    MeshNode leftArmShape = new MeshNode("Cube(left arm)", cube2);
    MeshNode rightArmShape = new MeshNode("Cube(right arm)", cube2);
    MeshNode leftLegShape = new MeshNode("Cube(leftleg)", cube);
    MeshNode rightLegShape = new MeshNode("Cube(rightleg)", cube);

    robot = new NameNode("root");
    NameNode body = new NameNode("body");
    NameNode head = new NameNode("head");
    NameNode leftarm = new NameNode("left arm");
    NameNode rightarm = new NameNode("right arm");
    NameNode leftleg = new NameNode("left leg");
    NameNode rightleg = new NameNode("right leg");

    float bodyHeight = 3f;
    float bodyWidth = 2f;
    float bodyDepth = 1f;
    float headScale = 2f;
    float armLength = 3.5f;
    float armScale = 0.5f;
    float legLength = 3.5f;
    float legScale = 0.67f;

    robotMoveTranslate = new TransformNode("robot transform",Mat4Transform.translate(xPosition,0,0));
    TransformNode robotTranslate = new TransformNode("robot transform",Mat4Transform.translate(0,legLength,0));
    
    Mat4 m = Mat4Transform.scale(bodyWidth,bodyHeight,bodyDepth);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode bodyTransform = new TransformNode("body transform", m);
     
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0,bodyHeight,0));
    m = Mat4.multiply(m, Mat4Transform.scale(headScale,headScale,headScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode headTransform = new TransformNode("head transform", m);
   
    TransformNode leftArmTranslate = new TransformNode("leftarm translate", 
                                           Mat4Transform.translate((bodyWidth*0.5f)+(armScale*0.5f),bodyHeight,0));
    leftArmRotate = new TransformNode("leftarm rotate",Mat4Transform.rotateAroundX(180));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(armScale,armLength,armScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode leftArmScale = new TransformNode("leftarm scale", m);
    
    TransformNode rightArmTranslate = new TransformNode("rightarm translate", 
                                          Mat4Transform.translate(-(bodyWidth*0.5f)-(armScale*0.5f),bodyHeight,0));
    rightArmRotate = new TransformNode("rightarm rotate",Mat4Transform.rotateAroundX(180));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(armScale,armLength,armScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode rightArmScale = new TransformNode("rightarm scale", m);
    
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate((bodyWidth*0.5f)-(legScale*0.5f),0,0));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundX(180));
    m = Mat4.multiply(m, Mat4Transform.scale(legScale,legLength,legScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode leftlegTransform = new TransformNode("leftleg transform", m);

    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(-(bodyWidth*0.5f)+(legScale*0.5f),0,0));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundX(180));
    m = Mat4.multiply(m, Mat4Transform.scale(legScale,legLength,legScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode rightlegTransform = new TransformNode("rightleg transform", m);
        
    // make scene graph
    robot.addChild(robotMoveTranslate);
      robotMoveTranslate.addChild(robotTranslate);
        robotTranslate.addChild(body);
          body.addChild(bodyTransform);
            bodyTransform.addChild(bodyShape);
          body.addChild(head);
            head.addChild(headTransform);
            headTransform.addChild(headShape);
          body.addChild(leftarm);
            leftarm.addChild(leftArmTranslate);
            leftArmTranslate.addChild(leftArmRotate);
            leftArmRotate.addChild(leftArmScale);
            leftArmScale.addChild(leftArmShape);
          body.addChild(rightarm);
            rightarm.addChild(rightArmTranslate);
            rightArmTranslate.addChild(rightArmRotate);
            rightArmRotate.addChild(rightArmScale);
            rightArmScale.addChild(rightArmShape);
          body.addChild(leftleg);
            leftleg.addChild(leftlegTransform);
            leftlegTransform.addChild(leftLegShape);
          body.addChild(rightleg);
            rightleg.addChild(rightlegTransform);
            rightlegTransform.addChild(rightLegShape);
    
    robot.update();  // IMPORTANT - don't forget this
  }
 
  // Get perspective matrix in render in case aspect has changed as a result of reshape.
  // Could more to reshape instead, so only get if reshape happens.
 
  // Transforms may be altered each frame for objects so they are set in the render method. 
  // If the transforms do not change each frame, then the model matrix could set in initialise() and then only retrieved here,
  // although this depends if the same object is being used in multiple positions, in which case
  // the transforms would need updating for each use of the object.
  // For more efficiency, if the object is static, its vertices could be defined once in the correct world positions.
  
  public void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    updatePerspectiveMatrices();
    
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);

    floor.render(gl); 
    
    if (animation) updateLeftArm();
    robot.draw(gl);
    }
    
    tt1.setModelMatrix(getMforTT1());
    tt1.render(gl, light, viewPosition, perspective, view);
    tt2.setModelMatrix(getMforTT2());
    tt2.render(gl, light, viewPosition, perspective, view);
    tt3.setModelMatrix(getMforTT3());
    tt3.render(gl, light, viewPosition, perspective, view);
    tt4.setModelMatrix(getMforTT4());
    tt4.render(gl, light, viewPosition, perspective, view); 
    tt5.setModelMatrix(getMforTT5()); 
    tt5.render(gl, light, viewPosition, perspective, view);
    tt6.setModelMatrix(getMforTT6()); 
    tt6.render(gl, light, viewPosition, perspective, view);
  }
  
  private void updateLightColour() {
    double elapsedTime = getSeconds()-startTime;
    Vec3 lightColour = new Vec3();
    lightColour.x = (float)Math.sin(elapsedTime * 2.0f);
    lightColour.y = (float)Math.sin(elapsedTime * 0.7f);
    lightColour.z = (float)Math.sin(elapsedTime * 1.3f);
    Material m = light.getMaterial();
    m.setDiffuse(Vec3.multiply(lightColour,0.5f));
    m.setAmbient(Vec3.multiply(m.getDiffuse(),0.2f));
    light.setMaterial(m);
  }
  
  // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.4f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);
  }

  private Mat4 getModelMatrix(int i) {
    double elapsedTime = getSeconds()-startTime;
    Mat4 model = new Mat4(1);    
    float yAngle = (float)(elapsedTime*100*randoms[(i+637)%NUM_RANDOMS]);
    float multiplier = 12.0f;
    float x = multiplier*randoms[i%NUM_RANDOMS] - multiplier*0.5f;
    float y = 0.5f+ (multiplier*0.5f) + multiplier*randoms[(i+137)%NUM_RANDOMS] - multiplier*0.5f;
    float z = multiplier*randoms[(i+563)%NUM_RANDOMS] - multiplier*0.5f;
    model = Mat4.multiply(model, Mat4Transform.translate(x,y,z));
    model = Mat4.multiply(model, Mat4Transform.rotateAroundY(yAngle));
    return model;
  }
  
  // As the transforms do not change over time for this object, they could be stored once rather than continually being calculated
  private Mat4 getMforCube() {
    Mat4 model = new Mat4(1);
    model = Mat4.multiply(Mat4Transform.translate(0f,0.5f,0f), model);
    model = Mat4.multiply(Mat4Transform.scale(4f,4f,4f), model);
    return model;
  }
  
  // As the transforms do not change over time for this object, they could be stored once rather than continually being calculated
  // floor
  private Mat4 getMforTT1() {
    float size = 16f;
    Mat4 model = new Mat4(1);
    model = Mat4.multiply(Mat4Transform.scale(size,1f,size), model);
    return model;
  }
  
  // front wall
  private Mat4 getMforTT2() {
    float size = 16f;
    Mat4 model = new Mat4(1);
    model = Mat4.multiply(Mat4Transform.scale(size,1f,size), model);
    model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
    model = Mat4.multiply(Mat4Transform.translate(0,size*0.5f,-size*0.5f), model);
    return model;
  }

  // left wall
  private Mat4 getMforTT3() {
    float size = 16f;
    Mat4 model = new Mat4(1);
    model = Mat4.multiply(Mat4Transform.scale(size,1f,size), model);
    model = Mat4.multiply(Mat4Transform.rotateAroundY(90), model);
    model = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), model);
    model = Mat4.multiply(Mat4Transform.translate(-size*0.5f,size*0.5f,0), model);
    return model;
  }

  // right wall
  private Mat4 getMforTT4() {
    float size = 16f;
    Mat4 model = new Mat4(1);
    model = Mat4.multiply(Mat4Transform.scale(size,1f,size), model);
    model = Mat4.multiply(Mat4Transform.rotateAroundZ(90), model);
    model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
    model = Mat4.multiply(Mat4Transform.translate(size*0.5f,size*0.5f,0), model);
    return model;
  }

  // back wall
  private Mat4 getMforTT5() {
    float size = 16f;
    Mat4 model = new Mat4(1);
    model = Mat4.multiply(Mat4Transform.scale(size,1f,size), model);
    model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
    model = Mat4.multiply(Mat4Transform.rotateAroundY(180), model);
    model = Mat4.multiply(Mat4Transform.translate(0,size*0.5f,size*0.5f), model);
    return model;
  }

  // ceiling
  private Mat4 getMforTT6() {
    float size = 16f;
    Mat4 model = new Mat4(1);
    model = Mat4.multiply(Mat4Transform.scale(size,1f,size), model);
    model = Mat4.multiply(Mat4Transform.rotateAroundZ(180), model);
    model = Mat4.multiply(Mat4Transform.translate(0,size,0), model);
    return model;
  }
  
}