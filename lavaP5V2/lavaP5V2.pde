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
void setup(){
	size(displayWidth,displayHeight,FX2D);
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
		angleX[i] = random(0.1);
		angleY[i] = random(0.4);
		blobPos[i] =new PVector(random(blobs.width),random(blobs.height));
		blobs.ellipse(blobPos[i].x,blobPos[i].y,blobSize[i],blobSize[i]);
	}	
	blobs.endDraw();
	 blobsImg = blobs.get();
	 blurImg = blobsImg.copy();
	 // fastBlur(blobsImg,50);
	// fastBlur(blobsImg,int(map(mouseX,0,width,1,25)));
}

void draw(){
	background(0);
	animate();

	blurImg = blobsImg.copy();

	fastBlur(blurImg,int(map(mouseX,1,width,1,95)));
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

void animate(){
	blobs.beginDraw();
	blobs.background(0);
	blobs.fill(0,0,255);
	blobs.noStroke();
	
	for(int i = 0; i < amount; i ++){

		blobSize[i] = blobSize[i] + random(-0.5,0.5);
		angleX[i] +=random(0.09);
		angleY[i] +=random(0.081);
		blobPos[i] =new PVector(blobPos[i].x , blobPos[i].y );
		blobs.ellipse(blobPos[i].x + 16*sin((angleX[i])),blobPos[i].y+ 13* sin((angleY[i])),blobSize[i],blobSize[i]);
	}	
	blobs.endDraw();
	blobsImg = blobs.get();
}
