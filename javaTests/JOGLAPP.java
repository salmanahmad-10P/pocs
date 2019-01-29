import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;

import com.sun.opengl.util.Animator;

public class JOGLAPP implements GLEventListener {
  public static void main(String[] args) {
    new JOGLAPP();
  }
  
  Frame fr;
  GLCanvas canvas;
  
  public JOGLAPP() {
    fr = new Frame("JOGLAPP");
    canvas = new GLCanvas();
    canvas.addGLEventListener(this);
    fr.add(canvas);
    
    Animator anim = new Animator(canvas);
    
    fr.setSize(300,300);
    fr.setVisible(true);
    anim.start();
    
    fr.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        System.exit(0);
      }
    });
  }
  
  @Override
  public void init(GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();
    
    gl.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
    
    // init opengl here
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();
    
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    
    // draw things here
    
    gl.glFinish();
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {
    
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width,
      int height) {
    
  }
  
  
}

