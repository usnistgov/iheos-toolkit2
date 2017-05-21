package gov.nist.toolkit.desktop.client.legacy.widgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasRows;

/**
 * A scrolling pager that automatically increases the range every
 time the
 * scroll bar reaches the bottom.
 */
public class ScrollingPager extends AbstractPager
{
    private int incrementSize = 20;
    private int lastScrollPos = 0;
    private final ScrollPanel scrollable = new ScrollPanel();

    public ScrollingPager()
    {
        initWidget(scrollable);
        scrollable.setPixelSize(250, 350);
        scrollable.getElement().getStyle().setBorderWidth(1,
                Style.Unit.PX);
        scrollable.getElement().getStyle().setBorderColor("red");
        scrollable.getElement().setTabIndex(-1);

        // Handle scroll events.
        scrollable.addScrollHandler(new ScrollHandler()
        {
            public void onScroll(ScrollEvent p_event)
            {
                // If scrolling up, ignore the event.
                int oldScrollPos = lastScrollPos;
                lastScrollPos =
                        scrollable.getVerticalScrollPosition();
                if (oldScrollPos >= lastScrollPos)
                {
                    return;
                }
                HasRows display = getDisplay();
                if (display == null)
                {
                    return;
                }
                int maxScrollTop =
                        scrollable.getWidget().getOffsetHeight()
                                - scrollable.getOffsetHeight();
                if (lastScrollPos >= maxScrollTop)
                {
                    // We are near the end, so increase the page
                    int newPageSize =
                            Math.min(display.getVisibleRange()
                                            .getLength()
                                            + incrementSize,
                                    display.getRowCount());
                    display.setVisibleRange(0, newPageSize);
                }
            }
        });
    }

    public int getIncrementSize()
    {
        return incrementSize;
    }

    @Override
    public void setDisplay(HasRows display)
    {
        assert display instanceof Widget : "display must extend Widget";
        scrollable.setWidget((Widget)display);
        super.setDisplay(display);
    }

    public void setIncrementSize(int incrementSize)
    {
        this.incrementSize = incrementSize;
    }

    @Override
    protected void onRangeOrRowCountChanged()
    {
    }
}
