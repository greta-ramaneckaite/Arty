import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

public class Hand {
  private SGNode hand;

  
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

  float ringRHeight = 0.3f;
  float ringRWidth = 0.9f;
  float ringRDepth = 0.9f;
  

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
  MeshNode ringShape = new MeshNode("Sphere(ring)", sphere2);


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
  NameNode ring = new NameNode("ring");

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

    //ring
    TransformNode ringTranslate = new TransformNode("ring translate", Mat4Transform.translate(0, ringRHeight, 0));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(ringRWidth, ringRHeight, ringRDepth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode ringTransform = new TransformNode("ring transform", m);

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


  /* ---------------------------------------------------------------------------

    Hierarchy tree for the hand

  ----------------------------------------------------------------------------*/


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
                      ringProxRotateZ.addChild(ringTranslate);
                        ringTranslate.addChild(ring);
                          ring.addChild(ringTransform);
                            ringTransform.addChild(ringShape);
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