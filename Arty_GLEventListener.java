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

  private Camera camera;
  private Mat4 perspective;
  private Mesh floor, frontWall, rightWall, leftWall, backWall, ceiling, sphere, cube, cube2;
  private Light light;
  private SGNode robot;
  
  private float xPosition = 0;
  private TransformNode translateX, robotMoveTranslate, leftArmRotate, rightArmRotate;
  
  private void initialise(GL3 gl) {
    createRandomNumbers();
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/chequerboard.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/jade_specular.jpg");
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/wattBook.jpg");
    int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/wattBook_specular.jpg");

    int[] textureId7 = TextureLibrary.loadTexture(gl, "textures/floor.jpg");
    int[] textureId8 = TextureLibrary.loadTexture(gl, "textures/wall.jpg");



    // make meshes
    floor = new TwoTriangles(gl, textureId0);
    floor.setModelMatrix(Mat4Transform.scale(16,1,16));  

      // newwwww
    frontWall = new TwoTriangles(gl, textureId0);
    frontWall.setModelMatrix(getMforTT2());
    rightWall = new TwoTriangles(gl, textureId0);
    rightWall.setModelMatrix(getMforTT4());
    leftWall = new TwoTriangles(gl, textureId0);
    leftWall.setModelMatrix(getMforTT3());
    backWall = new TwoTriangles(gl, textureId0);
    backWall.setModelMatrix(getMforTT5());
    ceiling = new TwoTriangles(gl, textureId0);
    ceiling.setModelMatrix(getMforTT6());


    sphere = new Sphere(gl, textureId1, textureId2);
    cube = new Cube(gl, textureId3, textureId4);
    cube2 = new Cube(gl, textureId5, textureId6);

    light = new Light(gl);
    light.setCamera(camera);
    
    floor.setLight(light);
    floor.setCamera(camera);

    // newwwwww
    frontWall.setLight(light);
    frontWall.setCamera(camera);
    rightWall.setLight(light);
    rightWall.setCamera(camera);
    leftWall.setLight(light);
    leftWall.setCamera(camera);
    backWall.setLight(light);
    backWall.setCamera(camera);
    // also newww
    ceiling.setLight(light);
    ceiling.setCamera(camera);

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
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    updatePerspectiveMatrices();
    
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);

    floor.render(gl);

    // neeewww
    frontWall.render(gl); 
    rightWall.render(gl);
    leftWall.render(gl);
    backWall.render(gl);
    ceiling.render(gl);
    
    if (animation) updateLeftArm();
    robot.draw(gl);
  }
    
  private void updatePerspectiveMatrices() {
    // needs to be changed if user resizes the window
    perspective = Mat4Transform.perspective(45, aspect);
    light.setPerspective(perspective);
    floor.setPerspective(perspective);

    // newwww
    frontWall.setPerspective(perspective);
    rightWall.setPerspective(perspective);
    leftWall.setPerspective(perspective);
    backWall.setPerspective(perspective);
    ceiling.setPerspective(perspective);

    sphere.setPerspective(perspective);
    cube.setPerspective(perspective);
    cube2.setPerspective(perspective);
  }
  
  private void disposeMeshes(GL3 gl) {
    light.dispose(gl);
    floor.dispose(gl);

    // newww
    frontWall.dispose(gl);
    rightWall.dispose(gl);
    leftWall.dispose(gl);
    backWall.dispose(gl);
    ceiling.dispose(gl);

    sphere.dispose(gl);
    cube.dispose(gl);
    cube2.dispose(gl);
  }
  
  private void updateLeftArm() {
    double elapsedTime = getSeconds()-startTime;
    float rotateAngle = 180f+90f*(float)Math.sin(elapsedTime);
    leftArmRotate.setTransform(Mat4Transform.rotateAroundX(rotateAngle));
    leftArmRotate.update();
    rotateAngle = -rotateAngle;
    rightArmRotate.setTransform(Mat4Transform.rotateAroundX(rotateAngle));
    rightArmRotate.update();
  }
  
  // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);   
    //return new Vec3(5f,3.4f,5f);
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