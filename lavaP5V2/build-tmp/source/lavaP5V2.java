import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class lavaP5V2 extends PApplet {

// ==================================================
// Lava in processing... in process
// By ndr3svt
// Zurich December 2020
// After codepen concept [ html + css ] using blur and contrast filters
// https://codepen.io/Elyx0/pen/JLEvh
// ==================================================
PGraphics blobs;
PImage blobsImg ;
PImage blurImg;
PVector [] blobPos;
float [] blobSize;
float [] angleX;
float [] angleY;
int amount=30;
public void setup(){
	
	blobPos = new PVector[amount];
	blobSize = new float[amount];
	angleX = new float[amount];
	angleY = new float[amount];
	// downsizing this image helps to reduce calculations load
	blobs = createGraphics(displayWidth/4,displayHeight/4);
	blobs.beginDraw();
	blobs.background(0);
	blobs.fill(0,0,255);
	blobs.noStroke();
	// lloop to initialize positions and sizes of the single 'blobs'
	for(int i = 0; i < amount; i ++){
		blobSize[i] = random(20,80);
		angleX[i] = random(0.1f);
		angleY[i] = random(0.4f);
		blobPos[i] =new PVector(random(blobs.width),random(blobs.height));
		blobs.ellipse(blobPos[i].x,blobPos[i].y,blobSize[i],blobSize[i]);
	}	
	blobs.endDraw();
	 blobsImg = blobs.get();
	 blurImg = blobsImg.copy();
	 // fastBlur(blobsImg,50);
	// fastBlur(blobsImg,int(map(mouseX,0,width,1,25)));
}

public void draw(){
	background(0);
	animate();

	blurImg = blobsImg.copy();

	fastBlur(blurImg,PApplet.parseInt(map(mouseX,1,width,1,95)));
	fastContrast(blurImg,100, color(0,0,100));
	// upsize the image for better resolution lower performance
	// blurImg.resize(width,height);
	// apply final blur for antialiasing - between 1 to 5
	fastBlur(blurImg,2);
	// fastContrast(blurImg,10);

	image(blurImg,0,0,width,height);


	fill(0,0,255);
	textSize(40);
	text(frameRate,25,50);
}

public void animate(){
	blobs.beginDraw();
	blobs.background(0);
	blobs.fill(0,0,255);
	blobs.noStroke();
	
	for(int i = 0; i < amount; i ++){

		blobSize[i] = blobSize[i] + random(-0.5f,0.5f);
		angleX[i] +=random(0.09f);
		angleY[i] +=random(0.081f);
		blobPos[i] =new PVector(blobPos[i].x , blobPos[i].y );
		blobs.ellipse(blobPos[i].x + 16*sin((angleX[i])),blobPos[i].y+ 13* sin((angleY[i])),blobSize[i],blobSize[i]);
	}	
	blobs.endDraw();
	blobsImg = blobs.get();
}

// ==================================================
// fast Contrast v 1.0
// By ndr3svt
// ==================================================
public void fastContrast(PImage img,float threshold, int _target){
	img.loadPixels();
	for (int i = 0; i < img.pixels.length; i ++){
    // reading the actual color
		int c=img.pixels[i];
		// color target = color(255,0,0);
		PVector colorA = new PVector(red(c),green(c),blue(c));
		PVector colorTarget = new PVector(red(_target),green(_target),blue(_target));
		if(colorA.dist(colorTarget)<threshold){
			img.pixels[i]=_target;
		}else{
			img.pixels[i]=color(0);
		}
	}
	img.updatePixels();
}
// ==================================================

// ==================================================
// Super Fast Blur v1.1
// By Mario Klingemann
// ==================================================
public void fastBlur(PImage img, int radius)
{
  if (radius<1) {
    return;
  }
  int w=img.width;
  int h=img.height;
  int wm=w-1;
  int hm=h-1;
  int wh=w*h;
  int div=radius+radius+1;
  int r[]=new int[wh];
  int g[]=new int[wh];
  int b[]=new int[wh];
  int rsum, gsum, bsum, x, y, i, p, p1, p2, yp, yi, yw;
  int vmin[] = new int[max(w, h)];
  int vmax[] = new int[max(w, h)];
  // int[] pix2=img.pixels;
  
  int dv[]=new int[256*div];
  for (i=0; i<256*div; i++) {
    dv[i]=(i/div);
  }

  yw=yi=0;

  for (y=0; y<h; y++) {
    rsum=gsum=bsum=0;
    for (i=-radius; i<=radius; i++) {
      p=img.pixels[yi+min(wm, max(i, 0))];
      rsum+=(p & 0xff0000)>>16;
      gsum+=(p & 0x00ff00)>>8;
      bsum+= p & 0x0000ff;
    }
    for (x=0; x<w; x++) {

      r[yi]=dv[rsum];
      g[yi]=dv[gsum];
      b[yi]=dv[bsum];

      if (y==0) {
        vmin[x]=min(x+radius+1, wm);
        vmax[x]=max(x-radius, 0);
      }
      p1=img.pixels[yw+vmin[x]];
      p2=img.pixels[yw+vmax[x]];

      rsum+=((p1 & 0xff0000)-(p2 & 0xff0000))>>16;
      gsum+=((p1 & 0x00ff00)-(p2 & 0x00ff00))>>8;
      bsum+= (p1 & 0x0000ff)-(p2 & 0x0000ff);
      yi++;
    }
    yw+=w;
  }

  for (x=0; x<w; x++) {
    rsum=gsum=bsum=0;
    yp=-radius*w;
    for (i=-radius; i<=radius; i++) {
      yi=max(0, yp)+x;
      rsum+=r[yi];
      gsum+=g[yi];
      bsum+=b[yi];
      yp+=w;
    }
    yi=x;
    for (y=0; y<h; y++) {
      img.pixels[yi]=0xff000000 | (dv[rsum]<<16) | (dv[gsum]<<8) | dv[bsum];
      if (x==0) {
        vmin[y]=min(y+radius+1, hm)*w;
        vmax[y]=max(y-radius, 0)*w;
      }
      p1=x+vmin[y];
      p2=x+vmax[y];

      rsum+=r[p1]-r[p2];
      gsum+=g[p1]-g[p2];
      bsum+=b[p1]-b[p2];

      yi+=w;
    }
  }
}
  public void settings() { 	size(displayWidth,displayHeight,FX2D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "lavaP5V2" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
