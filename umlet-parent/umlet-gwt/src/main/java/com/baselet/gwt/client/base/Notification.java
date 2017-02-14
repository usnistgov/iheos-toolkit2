package com.baselet.gwt.client.base;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Notification extends DialogBox {

    public void showFeatureNotSupported(String text, boolean fadeOut) {
        frameMessage(new HTML(text));
    }

    public void showInfo(String text) {
       frameMessage(new HTML(text));
    }

    private void frameMessage(Widget content) {
        // DialogBox is a SimplePanel, so you have to set its widget property to
        // whatever you want its contents to be.

        Widget ok = getOkBtn("Ok");

        VerticalPanel verticalPanel = new VerticalPanel();
        if (content!=null)
            verticalPanel.add(content);
        verticalPanel.add(ok);

        setWidget(verticalPanel);
        center();
        setModal(true);

        show();
    }

    ClickHandler removeParentClickHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent clickEvent) {
            removeFromParent();
        }
    };
    private Widget getOkBtn(String buttonText) {
        Button ok = new Button(buttonText);
        ok.addClickHandler(removeParentClickHandler);
        ok.setFocus(true);
        return ok;
    }
}





/*
public class Notification {

	private static String lastShownFeatureNotSupportedText;
	private static Element element = RootPanel.get("featurewarning").getElement();

	public static void showFeatureNotSupported(String text, boolean fadeOut) {
		if (text.equals(lastShownFeatureNotSupportedText)) {
			return; // don't repeat the last warning
		}
		lastShownFeatureNotSupportedText = text;
		element.getStyle().setColor("red");
		element.setInnerHTML(text);
		if (fadeOut) {
			ElementFader.fade(element, 1, 0, 7000, 3000);
		}
	}

	public static void showInfo(String text) {
		element.getStyle().setColor("blue");
		element.setInnerHTML(text);
		ElementFader.fade(element, 1, 0, 4000, 2000);
	}

	private static class ElementFader {
		private static int stepCount = 0;
		private static Timer timer;
		private static Timer timerFader;

		public synchronized static void fade(final Element element, final float startOpacity, final float endOpacity, final int delay, final int totalTimeMillis) {
			if (timer != null) {
				timer.cancel();
			}
			if (timerFader != null) {
				timerFader.cancel();
			}
			DOM.setStyleAttribute(element, "opacity", Float.toString(startOpacity));// set start opacity now to make sure the opacity of an interrupted previous timer is overwritten
			timer = new Timer() {
				@Override
				public void run() {
					fade(element, startOpacity, endOpacity, totalTimeMillis);
				}
			};
			timer.schedule(delay);
		}

		private static void fade(final Element element, final float startOpacity, final float endOpacity, final int totalTimeMillis) {
			final int numberOfSteps = 30;
			int stepLengthMillis = totalTimeMillis / numberOfSteps;
			stepCount = 0;
			final float deltaOpacity = (endOpacity - startOpacity) / numberOfSteps;
			timerFader = new Timer() {
				@Override
				public void run() {
					float opacity = startOpacity + stepCount * deltaOpacity;
					DOM.setStyleAttribute(element, "opacity", Float.toString(opacity));

					stepCount++;
					if (stepCount == numberOfSteps) {
						DOM.setStyleAttribute(element, "opacity", Float.toString(endOpacity));
						cancel();
					}
				}
			};
			timerFader.scheduleRepeating(stepLengthMillis);
		}
	}
}
*/
