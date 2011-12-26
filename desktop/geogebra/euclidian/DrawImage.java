/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * DrawPoint.java
 *
 * Created on 11. Oktober 2001, 23:59
 */

package geogebra.euclidian;

import geogebra.common.euclidian.Drawable;
import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoPoint2;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

/**
 * 
 * @author Markus
 * @version
 */
public final class DrawImage extends Drawable {

	private GeoImage geoImage;
	boolean isVisible;
	private Image image;

	boolean absoluteLocation;
	private AlphaComposite alphaComp;
	private float alpha = -1;
	private boolean isInBackground = false;
	private AffineTransform at, atInverse, tempAT;
	private boolean needsInterpolationRenderingHint;
	private int screenX, screenY;
	private Rectangle boundingBox;
	private GeneralPath highlighting;

	public DrawImage(EuclidianView view, GeoImage geoImage) {
		this.view = view;
		this.geoImage = geoImage;
		geo = geoImage;

		// temp
		at = new AffineTransform();
		tempAT = new AffineTransform();
		boundingBox = new Rectangle();

		selStroke = geogebra.common.factories.AwtFactory.prototype.newMyBasicStroke(1.5f);

		update();
	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();

		if (!isVisible)
			return;

		if (geo.getAlphaValue() != alpha) {
			alpha = geo.getAlphaValue();
			alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					alpha);
		}

		image = geogebra.awt.BufferedImage.getAwtBufferedImage(geoImage
				.getFillImage());
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		absoluteLocation = geoImage.isAbsoluteScreenLocActive();

		// ABSOLUTE SCREEN POSITION
		if (absoluteLocation) {
			screenX = geoImage.getAbsoluteScreenLocX();
			screenY = geoImage.getAbsoluteScreenLocY() - height;
			labelRectangle.setBounds(screenX, screenY, width, height);
		}

		// RELATIVE SCREEN POSITION
		else {
			GeoPoint2 A = geoImage.getCorner(0);
			GeoPoint2 B = geoImage.getCorner(1);
			GeoPoint2 D = geoImage.getCorner(2);

			double ax = 0;
			double ay = 0;
			if (A != null) {
				if (!A.isDefined()) {
					isVisible = false;
					return;
				}
				ax = A.inhomX;
				ay = A.inhomY;
			}

			// set transform according to corners
			at.setTransform(geogebra.awt.AffineTransform.getAwtAffineTransform(view.getCoordTransform())); // last transform: real world
													// -> screen
			at.translate(ax, ay); // translate to first corner A

			if (B == null) {
				// we only have corner A
				if (D == null) {
					// use original pixel width and heigt of image
					at.scale(view.getInvXscale(), -view.getInvXscale());
				}
				// we have corners A and D
				else {
					if (!D.isDefined()) {
						isVisible = false;
						return;
					}
					// rotate to coord system (-ADn, AD)
					double ADx = D.inhomX - ax;
					double ADy = D.inhomY - ay;
					tempAT.setTransform(ADy, -ADx, ADx, ADy, 0, 0);
					at.concatenate(tempAT);

					// scale height of image to 1
					double yscale = 1.0 / height;
					at.scale(yscale, -yscale);
				}
			} else {
				if (!B.isDefined()) {
					isVisible = false;
					return;
				}

				// we have corners A and B
				if (D == null) {
					// rotate to coord system (AB, ABn)
					double ABx = B.inhomX - ax;
					double ABy = B.inhomY - ay;
					tempAT.setTransform(ABx, ABy, -ABy, ABx, 0, 0);
					at.concatenate(tempAT);

					// scale width of image to 1
					double xscale = 1.0 / width;
					at.scale(xscale, -xscale);
				} else { // we have corners A, B and D
					if (!D.isDefined()) {
						isVisible = false;
						return;
					}

					// shear to coord system (AB, AD)
					double ABx = B.inhomX - ax;
					double ABy = B.inhomY - ay;
					double ADx = D.inhomX - ax;
					double ADy = D.inhomY - ay;
					tempAT.setTransform(ABx, ABy, ADx, ADy, 0, 0);
					at.concatenate(tempAT);

					// scale width and height of image to 1
					at.scale(1.0 / width, -1.0 / height);
				}
			}

			// move image up so that A becomes lower left corner
			at.translate(0, -height);
			labelRectangle.setBounds(0, 0, width, height);

			// calculate bounding box for isInside
			boundingBox.setBounds(0, 0, width, height);
			Shape shape = at.createTransformedShape(boundingBox);
			boundingBox = shape.getBounds();

			try {
				// for hit testing
				atInverse = at.createInverse();
			} catch (NoninvertibleTransformException e) {
				isVisible = false;
				return;
			}

			// improve rendering for sheared and scaled images (translations
			// don't need this)
			// turns false if the image doen't want interpolation
			needsInterpolationRenderingHint = (geoImage.isInterpolate())
					&& !(AbstractKernel.isEqual(at.getScaleX(), 1.0,
							AbstractKernel.MAX_PRECISION)
							&& AbstractKernel.isEqual(at.getScaleY(), 1.0,
									AbstractKernel.MAX_PRECISION)
							&& AbstractKernel.isEqual(at.getShearX(), 0.0,
									AbstractKernel.MAX_PRECISION) && AbstractKernel
								.isEqual(at.getShearY(), 0.0,
										AbstractKernel.MAX_PRECISION));
		}

		if (isInBackground != geoImage.isInBackground()) {
			isInBackground = !isInBackground;
			if (isInBackground) {
				((EuclidianView)view).addBackgroundImage(this);
			} else {
				((EuclidianView)view).removeBackgroundImage(this);
				view.updateBackgroundImage();
			}
		}

		if (isInBackground)
			view.updateBackgroundImage();
	}

	@Override
	final public void draw(geogebra.common.awt.Graphics2D g3) {
		Graphics2D g2 = geogebra.awt.Graphics2D.getAwtGraphics(g3);
		if (isVisible) {
			Composite oldComp = g2.getComposite();
			if (alpha >= 0f && alpha < 1f) {
				if (alphaComp == null)
					alphaComp = AlphaComposite.getInstance(
							AlphaComposite.SRC_OVER, alpha);
				g2.setComposite(alphaComp);
			}

			if (absoluteLocation) {
				g2.drawImage(image, screenX, screenY, null);
				if (!isInBackground && geo.doHighlighting()) {
					// draw rectangle around image
					g3.setStroke(selStroke);
					g3.setPaint(geogebra.awt.Color.lightGray);
					g2.draw(geogebra.awt.Rectangle.getAWTRectangle(labelRectangle));
				}
			} else {
				AffineTransform oldAT = g2.getTransform();
				g2.transform(at);

				// improve rendering quality for transformed images
				Object oldInterpolationHint = g2
						.getRenderingHint(RenderingHints.KEY_INTERPOLATION);

				if (oldInterpolationHint == null)
					oldInterpolationHint = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

				if (needsInterpolationRenderingHint) {
					// improve rendering quality for transformed images
					g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
							RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				}

				// g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

				g2.drawImage(image, 0, 0, null);
				if (!isInBackground && geo.doHighlighting()) {
					// draw rectangle around image
					g3.setStroke(selStroke);
					g3.setPaint(geogebra.common.awt.Color.lightGray);

					// changed to code below so that the line thicknesses aren't
					// transformed
					// g2.draw(labelRectangle);

					// draw parallelogram around edge
					drawHighlighting(at, g2);
					Point2D corner1 = new Point2D.Double(
							labelRectangle.getMinX(), labelRectangle.getMinY());
					Point2D corner2 = new Point2D.Double(
							labelRectangle.getMinX(), labelRectangle.getMaxY());
					Point2D corner3 = new Point2D.Double(
							labelRectangle.getMaxX(), labelRectangle.getMaxY());
					Point2D corner4 = new Point2D.Double(
							labelRectangle.getMaxX(), labelRectangle.getMinY());
					at.transform(corner1, corner1);
					at.transform(corner2, corner2);
					at.transform(corner3, corner3);
					at.transform(corner4, corner4);
					if (highlighting == null)
						highlighting = new GeneralPath();
					else
						highlighting.reset();
					highlighting.moveTo((float) corner1.getX(),
							(float) corner1.getY());
					highlighting.lineTo((float) corner2.getX(),
							(float) corner2.getY());
					highlighting.lineTo((float) corner3.getX(),
							(float) corner3.getY());
					highlighting.lineTo((float) corner4.getX(),
							(float) corner4.getY());
					highlighting.lineTo((float) corner1.getX(),
							(float) corner1.getY());
					g2.setTransform(oldAT);
					g2.draw(highlighting);

				}

				// reset previous values
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						oldInterpolationHint);
				g2.setTransform(oldAT);
			}

			g2.setComposite(oldComp);
		}
	}

	private void drawHighlighting(AffineTransform at2, Graphics2D g2) {
		// TODO Auto-generated method stub

	}

	boolean isInBackground() {
		return geoImage.isInBackground();
	}

	/**
	 * was this object clicked at? (mouse pointer location (x,y) in screen
	 * coords)
	 */
	@Override
	final public boolean hit(int x, int y) {
		if (!isVisible || geoImage.isInBackground())
			return false;

		hitCoords[0] = x;
		hitCoords[1] = y;

		// convert screen to image coordinate system
		if (!geoImage.isAbsoluteScreenLocActive()) {
			atInverse.transform(hitCoords, 0, hitCoords, 0, 1);
		}
		return labelRectangle.contains(hitCoords[0], hitCoords[1]);
	}

	private double[] hitCoords = new double[2];

	@Override
	final public boolean isInside(geogebra.common.awt.Rectangle rect) {
		if (!isVisible || geoImage.isInBackground())
			return false;
		return geogebra.awt.Rectangle.getAWTRectangle(rect).contains(boundingBox);
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	final public geogebra.common.awt.Rectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}
		return new geogebra.awt.Rectangle(boundingBox);
	}

	/**
	 * Returns false
	 */
	@Override
	public boolean hitLabel(int x, int y) {
		return false;
	}

	@Override
	final public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	final public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

}
