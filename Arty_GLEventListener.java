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
  private Mesh cube, tt1, tt2, tt3, tt4;
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
    tt1.dispose(gl);
    tt2.dispose(gl);
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
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  public void initialise(GL3 gl) {
    createRandomNumbers();
    int[] textureId0 = TextureLibrary.loadTexture(gl, "container2.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "container2_specular.jpg");

    // Walls and floor
    int[] textureId2 = TextureLibrary.loadTexture(gl, "chequerboard.jpg");
    // front wall
    int[] textureId3 = TextureLibrary.loadTexture(gl, "cloud.jpg");
    // new right wall
    int[] textureId4 = TextureLibrary.loadTexture(gl, "wattBook.jpg");
    // new back wall
    int[] textureId5 = TextureLibrary.loadTexture(gl, "wattBook.jpg");

    cube = new Cube(gl, textureId0, textureId1);
    tt1 = new TwoTriangles(gl, textureId2);
    tt2 = new TwoTriangles(gl, textureId3);
    tt3 = new TwoTriangles(gl, textureId4);
    tt4 = new TwoTriangles(gl, textureId5);
    light = new Light(gl);
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
        
    Mat4 perspective = Mat4Transform.perspective(45, aspect);
    Mat4 view = camera.getViewMatrix();
    Vec3 viewPosition = camera.getPosition();
    
    //updateLightColour();
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl, perspective, view);
    
    for (int i=0; i<100; ++i) {
      cube.setModelMatrix(getModelMatrix(i));
      cube.render(gl, light, viewPosition, perspective, view);
    }
    
    tt1.setModelMatrix(getMforTT1());       // possibly changing cube transform each frame
    tt1.render(gl, light, viewPosition, perspective, view);
    tt2.setModelMatrix(getMforTT2());       // possibly changing cube transform each frame
    tt2.render(gl, light, viewPosition, perspective, view);
    tt1.setModelMatrix(getMforTT3());       // possibly changing cube transform each frame
    tt1.render(gl, light, viewPosition, perspective, view);
    tt3.setModelMatrix(getMforTT4());       // possibly changing cube transform each frame
    tt3.render(gl, light, viewPosition, perspective, view);
    tt4.setModelMatrix(getMforTT5());       // possibly changing cube transform each frame
    tt4.render(gl, light, viewPosition, perspective, view); 
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
  private Mat4 getMforTT1() {
    float size = 16f;
    Mat4 model = new Mat4(1);
    model = Mat4.multiply(Mat4Transform.scale(size,1f,size), model);
    return model;
  }
  
  // As the transforms do not change over time for this object, they could be stored once rather than continually being calculated
  private Mat4 getMforTT2() {
    float size = 16f;
    Mat4 model = new Mat4(1);
    model = Mat4.multiply(Mat4Transform.scale(size,1f,size), model);
    model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
    model = Mat4.multiply(Mat4Transform.translate(0,size*0.5f,-size*0.5f), model);
    return model;
  }

  private Mat4 getMforTT3() {
    float size = 16f;
    Mat4 model = new Mat4(1);
    model = Mat4.multiply(Mat4Transform.scale(size,1f,size), model);
    model = Mat4.multiply(Mat4Transform.rotateAroundY(90), model);
    model = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), model);
    model = Mat4.multiply(Mat4Transform.translate(-size*0.5f,size*0.5f,0), model);
    return model;
  }

  private Mat4 getMforTT4() {
    float size = 16f;
    Mat4 model = new Mat4(1);
    model = Mat4.multiply(Mat4Transform.scale(size,1f,size), model);
    model = Mat4.multiply(Mat4Transform.rotateAroundZ(90), model);
    model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
    model = Mat4.multiply(Mat4Transform.translate(size*0.5f,size*0.5f,0), model);
    return model;
  }

  private Mat4 getMforTT5() {
    float size = 16f;
    Mat4 model = new Mat4(1);
    model = Mat4.multiply(Mat4Transform.scale(size,1f,size), model);
    model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
    model = Mat4.multiply(Mat4Transform.rotateAroundY(180), model);
    model = Mat4.multiply(Mat4Transform.translate(0,size*0.5f,size*0.5f), model);
    return model;
  }
  
}