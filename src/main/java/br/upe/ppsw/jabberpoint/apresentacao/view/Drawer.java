package br.upe.ppsw.jabberpoint.apresentacao.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.upe.ppsw.jabberpoint.apresentacao.model.Slide;
import br.upe.ppsw.jabberpoint.apresentacao.model.SlideItem;
import br.upe.ppsw.jabberpoint.apresentacao.model.Style;

public class Drawer {
	
	Drawer() {};
	
/*	TextDrawer */
	public void draw(int x, int y, float scale, Graphics g, Style myStyle, ImageObserver o,
			String text, List<TextLayout> layouts) {
	    if (text == null || text.length() == 0) {
	      return;
	    }

//	    List<TextLayout> layouts = getLayouts(g, myStyle, scale);
	    Point pen = new Point(x + (int) (myStyle.indent * scale), y + (int) (myStyle.leading * scale));

	    Graphics2D g2d = (Graphics2D) g;
	    g2d.setColor(myStyle.color);

	    Iterator<TextLayout> it = layouts.iterator();

	    while (it.hasNext()) {
	      TextLayout layout = it.next();

	      pen.y += layout.getAscent();
	      layout.draw(g2d, pen.x, pen.y);

	      pen.y += layout.getDescent();
	    }
	}

/*	BitmapDrawer */
	public void draw(int x, int y, float scale, Graphics g, Style myStyle, ImageObserver observer,
			BufferedImage bufferedImage) {
	    int width = x + (int) (myStyle.indent * scale);
	    int height = y + (int) (myStyle.leading * scale);

	    g.drawImage(bufferedImage, width, height, (int) (bufferedImage.getWidth(observer) * scale),
	        (int) (bufferedImage.getHeight(observer) * scale), observer);
	}
	
/*	SlideDrawer */
	public void draw(Graphics g, Rectangle area, ImageObserver view,
		  String text, List<TextLayout> layouts, int size, Slide slide, TextItem textItem, SlideItem slideItem) {
		float scale = getScale(area);

		int y = area.y;

//		SlideItem slideItem = this.title;
		Style style = Style.getStyle(textItem.getLevel());
		draw(area.x, y, scale, g, style, view, text, layouts);

		y += textItem.getBoundingBox(g, view, scale, style).height;

		for (int number = 0; number < size; number++) {
			slideItem = (SlideItem) slide.getSlideItems().elementAt(number);

			style = Style.getStyle(slideItem.getLevel());
		    slideItem.draw(area.x, y, scale, g, style, view);

		    y += slideItem.getBoundingBox(g, view, scale, style).height;
		}
	}

	private float getScale(Rectangle area) {
		return Math.min(((float) area.width) / ((float) Slide.WIDTH),
	    ((float) area.height) / ((float) Slide.HEIGHT));
	  }
	
/*	BitmapBoundBox */
	public Rectangle getBoundingBox(Graphics g, ImageObserver observer, float scale, Style myStyle, BufferedImage bufferedImage) {
	    return new Rectangle((int) (myStyle.indent * scale), 0,
	        (int) (bufferedImage.getWidth(observer) * scale),
	        ((int) (myStyle.leading * scale)) + (int) (bufferedImage.getHeight(observer) * scale));
	}

/*	TextItemBoundBox */
	public Rectangle getBoundingBox(Graphics g, ImageObserver observer, float scale, Style myStyle, List<TextLayout> layouts) {
//		List<TextLayout> layouts = getLayouts(g, myStyle, scale);

		int xsize = 0, ysize = (int) (myStyle.leading * scale);

		Iterator<TextLayout> iterator = layouts.iterator();

		while (iterator.hasNext()) {
			TextLayout layout = iterator.next();
		    Rectangle2D bounds = layout.getBounds();

		    if (bounds.getWidth() > xsize) {
		    	xsize = (int) bounds.getWidth();
	        }

		    if (bounds.getHeight() > 0) {
		        ysize += bounds.getHeight();
		    }
		      ysize += layout.getLeading() + layout.getDescent();
		}

		return new Rectangle((int) (myStyle.indent * scale), 0, xsize, ysize);
	  }

	  private List<TextLayout> getLayouts(Graphics g, Style s, float scale, AttributedString attrStr, String text) {
		  List<TextLayout> layouts = new ArrayList<TextLayout>();

//		  AttributedString attrStr = getAttributedString(s, scale);
		  Graphics2D g2d = (Graphics2D) g;

		  FontRenderContext frc = g2d.getFontRenderContext();
		  LineBreakMeasurer measurer = new LineBreakMeasurer(attrStr.getIterator(), frc);

		  float wrappingWidth = (Slide.WIDTH - s.indent) * scale;

		  while (measurer.getPosition() < text.length()) {
			  TextLayout layout = measurer.nextLayout(wrappingWidth);
		      layouts.add(layout);
		  }

		  return layouts;
	  }
	
}
