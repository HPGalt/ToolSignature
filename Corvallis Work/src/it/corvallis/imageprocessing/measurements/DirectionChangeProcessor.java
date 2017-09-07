package it.corvallis.imageprocessing.measurements;

import ij.ImagePlus;

import java.util.Arrays;

public class DirectionChangeProcessor {
	private static final int[][] giroStandard 
	= new int[][] { { -1, -1 }, { 0, -1 }, { 1, -1 }, { 1, 0 }, { 1, 1 }, { 0, 1 }, { -1, 1 }, { -1, 0 } };
	private static final int PASSI_GIRO = 8;

	/** 
	 * The wrapped image as passed to the constructor.
	 */
	protected ImagePlus imagePlus;
	
	public DirectionChangeProcessor(final ImagePlus originalImagePlus) {
		this.imagePlus = originalImagePlus;
	}
	
	public double getOriginalDirectionChange() {
		return countDirectionChange(imagePlus);
	}
	
	private double countDirectionChange(ImagePlus image) {
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		byte[] pixels = (byte[]) image.getProcessor().getPixels();
		byte[] closedArray = new byte[pixels.length];
		Arrays.fill(closedArray, (byte) -1);
		double directionChangeCounter = 0;
		
		// Partiamo dal secondo pixel (in generale evitiamo il bordo
		// dell'immagine)
		int localCol = 0;
		int localRow = 0;
		int pixelPos = 0;
		for (; localRow < imageHeight; pixelPos++) {
		
			// Consideriamo solo i casi in cui il pixel corrente è nero
			if (pixels[pixelPos] == 0 && closedArray[pixelPos] == -1) {
				directionChangeCounter += pathFollow(pixels, pixelPos, imageWidth, closedArray);
			}
	
			localCol++;
			if (localCol >= imageWidth) {
				localCol = 0;
				localRow++;
			}
		}
	
		return directionChangeCounter;
	
	}
	
	private int pathFollow(byte[] pixels, int pixelPos, int imageWidth, byte[] closedArray) {
		int directionChangeCounter = -1;
		byte initialDirection=0;
	
		// Marchiamo il pixel di partenza in modo da riconoscerlo
		closedArray[pixelPos] = -2;
		
		int localCol = pixelPos % imageWidth;
		int localRow = pixelPos / imageWidth;
		for(;;) {
			for(byte currentDirection = initialDirection; currentDirection < initialDirection + PASSI_GIRO; currentDirection++){
				byte modCurrentDirection = (byte) (currentDirection % PASSI_GIRO);
				int[] passo = giroStandard[modCurrentDirection];
				
				// Ignoriamo i passi che escono dal bordo dell'immagine
				if(passo[0] < 0 && localCol==0 ||
				   passo[0] > 0 && localCol==imageWidth-1 ||
				   passo[1] < 0 && localRow==0 ||
				   passo[1] > 0 && pixelPos + imageWidth > pixels.length) {
					continue;
				}
				
				int pos = pixelPos + passo[1] * imageWidth + passo[0];
				
				// Seguiamo il percorso se il prossimo pixel è nero è non l'abbiamo già visitato
				if(pixels[pos] == 0 && closedArray[pos] == -1) {
					
					// TODO rivedere!!!!!
					if(modCurrentDirection != initialDirection){
						
						// Diamo un peso diverso a ogni cambio di direzione,
						// a seconda di quanto il cambiamento è marcato
						directionChangeCounter += PASSI_GIRO/2 - Math.abs(
								Math.max(initialDirection, modCurrentDirection)
								- (Math.min(initialDirection, modCurrentDirection)+PASSI_GIRO/2) );
						
					}
	
					closedArray[pos] = modCurrentDirection;
					pixelPos = pos;
					initialDirection = modCurrentDirection;
					currentDirection = (byte) (modCurrentDirection-1);
					localCol += passo[0];
					localRow += passo[1];
				}
			}
			
			// Abbiamo finito quando il pixel ha direzione -1, ossia
			// quando abbiamo fatto tutto il giro attorno al pixel di partenza
			if(closedArray[pixelPos] < 0)
				break;
			
			// Torniamo al pixel precedente
			byte backDirection = (byte) ((closedArray[pixelPos] + (PASSI_GIRO / 2)) % PASSI_GIRO);
			int[] passo = giroStandard[backDirection];
			initialDirection = (byte) ((closedArray[pixelPos]+1) % PASSI_GIRO);
			int pos = pixelPos + passo[1] * imageWidth + passo[0];
			pixelPos = pos;
			localCol += passo[0];
			localRow += passo[1];
		}
		
		return directionChangeCounter;
	}

}
