import gmaths.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class Arty extends JFrame implements ActionListener {
  
  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
  private GLCanvas canvas;
  private Arty_GLEventListener glEventListener;
  private final FPSAnimator animator; 
  private Camera camera;

  public static void main(String[] args) {
    Arty b1 = new Arty("Arty");
    b1.getContentPane().setPreferredSize(dimension);
    b1.pack();
    b1.setVisible(true);
  }

  public Arty(String textForTitleBar) {
    super(textForTitleBar);
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
    camera = new Camera(new Vec3(4f,12f,18f), Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    glEventListener = new Arty_GLEventListener(camera);
    canvas.addGLEventListener(glEventListener);
    canvas.addMouseMotionListener(new MyMouseInput(camera));
    canvas.addKeyListener(new MyKeyboardInput(camera));
    getContentPane().add(canvas, BorderLayout.CENTER);
    
    JMenuBar menuBar=new JMenuBar();
    this.setJMenuBar(menuBar);
      JMenu fileMenu = new JMenu("File");
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(this);
        fileMenu.add(quitItem);
    menuBar.add(fileMenu);
    
    JPanel p = new JPanel();
      JButton b = new JButton("camera X");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("camera Z");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("start");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("stop");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("DO G");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("rotate wrist");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("rotate palm z");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("index rotate x");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("index rotate z");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("middle rotate x");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("middle rotate z");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("ring rotate x");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("ring rotate z");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("pinky rotate x");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("pinky rotate z");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("thumb rotate y");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("thumb rotate z");
      b.addActionListener(this);
      p.add(b);
    this.add(p, BorderLayout.SOUTH);
    
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        animator.stop();
        remove(canvas);
        dispose();
        System.exit(0);
      }
    });
    animator = new FPSAnimator(canvas, 60);
    animator.start();
  }
  
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equalsIgnoreCase("camera X")) {
      camera.setCamera(Camera.CameraType.X);
      canvas.requestFocusInWindow();
    }
    else if (e.getActionCommand().equalsIgnoreCase("camera Z")) {
      camera.setCamera(Camera.CameraType.Z);
      canvas.requestFocusInWindow();
    }
    else if (e.getActionCommand().equalsIgnoreCase("start")) {
      glEventListener.startAnimation();
    }
    else if (e.getActionCommand().equalsIgnoreCase("stop")) {
      glEventListener.stopAnimation();
    }
    else if (e.getActionCommand().equalsIgnoreCase("DO G")) {
      glEventListener.doG();
    }
    else if (e.getActionCommand().equalsIgnoreCase("rotate wrist")) {
      glEventListener.doG();
    }
    else if (e.getActionCommand().equalsIgnoreCase("rotate palm z")) {
      glEventListener.rotatePalmZ();
    }
    else if (e.getActionCommand().equalsIgnoreCase("index rotate x")) {
      glEventListener.rotateIndexX();
    }
    else if (e.getActionCommand().equalsIgnoreCase("index rotate z")) {
      glEventListener.rotateIndexZ();
    }
    else if (e.getActionCommand().equalsIgnoreCase("middle rotate x")) {
      glEventListener.rotateMiddleX();
    }
    else if (e.getActionCommand().equalsIgnoreCase("middle rotate z")) {
      glEventListener.rotateMiddleZ();
    }
    else if (e.getActionCommand().equalsIgnoreCase("ring rotate x")) {
      glEventListener.rotateRingX();
    }
    else if (e.getActionCommand().equalsIgnoreCase("ring rotate z")) {
      glEventListener.rotateRingZ();
    }
    else if (e.getActionCommand().equalsIgnoreCase("pinky rotate x")) {
      glEventListener.rotatePinkyX();
    }
    else if (e.getActionCommand().equalsIgnoreCase("pinky rotate z")) {
      glEventListener.rotatePinkyZ();
    }
    else if (e.getActionCommand().equalsIgnoreCase("thumb rotate y")) {
      glEventListener.rotateThumbY();
    }
    else if (e.getActionCommand().equalsIgnoreCase("thumb rotate z")) {
      glEventListener.rotateThumbZ();
    }
    else if(e.getActionCommand().equalsIgnoreCase("quit"))
      System.exit(0);
  }
  
}
 
class MyKeyboardInput extends KeyAdapter  {
  private Camera camera;
  
  public MyKeyboardInput(Camera camera) {
    this.camera = camera;
  }
  
  public void keyPressed(KeyEvent e) {
    Camera.Movement m = Camera.Movement.NO_MOVEMENT;
    switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:  m = Camera.Movement.LEFT;  break;
      case KeyEvent.VK_RIGHT: m = Camera.Movement.RIGHT; break;
      case KeyEvent.VK_UP:    m = Camera.Movement.UP;    break;
      case KeyEvent.VK_DOWN:  m = Camera.Movement.DOWN;  break;
      case KeyEvent.VK_A:  m = Camera.Movement.FORWARD;  break;
      case KeyEvent.VK_Z:  m = Camera.Movement.BACK;  break;
    }
    camera.keyboardInput(m);
  }
}

class MyMouseInput extends MouseMotionAdapter {
  private Point lastpoint;
  private Camera camera;
  
  public MyMouseInput(Camera camera) {
    this.camera = camera;
  }
  
    /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */    
  public void mouseDragged(MouseEvent e) {
    Point ms = e.getPoint();
    float sensitivity = 0.001f;
    float dx=(float) (ms.x-lastpoint.x)*sensitivity;
    float dy=(float) (ms.y-lastpoint.y)*sensitivity;
    //System.out.println("dy,dy: "+dx+","+dy);
    if (e.getModifiers()==MouseEvent.BUTTON1_MASK)
      camera.updateYawPitch(dx, -dy);
    lastpoint = ms;
  }

  /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */  
  public void mouseMoved(MouseEvent e) {   
    lastpoint = e.getPoint(); 
  }
}