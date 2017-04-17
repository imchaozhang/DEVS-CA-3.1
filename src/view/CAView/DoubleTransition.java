package view.CAView;

import javafx.animation.Transition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * This {@code Transition} creates a fade effect animation that spans its
 * {@code duration}. This is done by updating the {@code opacity} variable of
 * the {@code node} at regular interval.
 * <p>
 * It starts from the {@code fromValue} if provided else uses the {@code node}'s
 * {@code opacity} value.
 * <p>
 * It stops at the {@code toValue} value if provided else it will use start
 * value plus {@code byValue}.
 * <p>
 * The {@code toValue} takes precedence if both {@code toValue} and
 * {@code byValue} are specified.
 * 
 * <p>
 * Code Segment Example:
 * </p>
 * 
 * <pre>
 * <code>
 * import javafx.scene.shape.*;
 * import javafx.animation.transition.*;
 * 
 * ...
 * 
 *     Rectangle rect = new Rectangle (100, 40, 100, 100);
 *     rect.setArcHeight(50);
 *     rect.setArcWidth(50);
 *     rect.setFill(Color.VIOLET);
 * 
 *     DoubleTransition ft = new DoubleTransition(Duration.millis(3000), rect);
 *     ft.setFromValue(1.0);
 *     ft.setToValue(0.3);
 *     ft.setCycleCount(4);
 *     ft.setAutoReverse(true);
 * 
 *     ft.play();
 * 
 * ...
 * 
 * </code>
 * </pre>
 * 
 * @see Transition
 * @see Animation
 * 
 * @since JavaFX 2.0
 */
public final class DoubleTransition extends Transition {
    private static final double EPSILON = 1e-12;

    private double start;
    private double delta;

    /**
     * The target node of this {@code Transition}.
     * <p>
     * It is not possible to change the target {@code node} of a running
     * {@code DoubleTransition}. If the value of {@code node} is changed for a
     * running {@code DoubleTransition}, the animation has to be stopped and
     * started again to pick up the new value.
     */
    private DoubleProperty node;
    
    public final void setNode(DoubleProperty value) {
        if ((node != null) || (value != null /* DEFAULT_NODE */)) {
            node.set(value.doubleValue());
        }
    }
    
    
  
    private Double cachedNode;

    /**
     * The duration of this {@code DoubleTransition}.
     * <p>
     * It is not possible to change the {@code duration} of a running
     * {@code DoubleTransition}. If the value of {@code duration} is changed for a
     * running {@code DoubleTransition}, the animation has to be stopped and
     * started again to pick up the new value.
     * <p>
     * Note: While the unit of {@code duration} is a millisecond, the
     * granularity depends on the underlying operating system and will in
     * general be larger. For example animations on desktop systems usually run
     * with a maximum of 60fps which gives a granularity of ~17 ms.
     *
     * Setting duration to value lower than {@link Duration#ZERO} will result
     * in {@link IllegalArgumentException}.
     *
     * @defaultValue 400ms
     */
    private ObjectProperty<Duration> duration;
    private static final Duration DEFAULT_DURATION = Duration.millis(400);

    public final void setDuration(Duration value) {
        if ((duration != null) || (!DEFAULT_DURATION.equals(value))) {
            durationProperty().set(value);
        }
    }

    public final Duration getDuration() {
        return (duration == null)? DEFAULT_DURATION : duration.get();
    }

    public final ObjectProperty<Duration> durationProperty() {
        if (duration == null) {
            duration = new ObjectPropertyBase<Duration>(DEFAULT_DURATION) {

                @Override
                public void invalidated() {
                    try {
                        setCycleDuration(getDuration());
                    } catch (IllegalArgumentException e) {
                        if (isBound()) {
                            unbind();
                        }
                        set(getCycleDuration());
                        throw e;
                    }
                }

                @Override
                public Object getBean() {
                    return DoubleTransition.this;
                }

                @Override
                public String getName() {
                    return "duration";
                }
            };
        }
        return duration;
    }

    /**
     * Specifies the start opacity value for this {@code DoubleTransition}.
     * <p>
     * It is not possible to change {@code fromValue} of a running
     * {@code DoubleTransition}. If the value of {@code fromValue} is changed for
     * a running {@code DoubleTransition}, the animation has to be stopped and
     * started again to pick up the new value.
     * 
     * @defaultValue {@code Double.NaN}
     */
    private DoubleProperty fromValue;
    private static final double DEFAULT_FROM_VALUE = Double.NaN;

    public final void setFromValue(double value) {
        if ((fromValue != null) || (!Double.isNaN(value) /* DEFAULT_FROM_VALUE */ )) {
            fromValueProperty().set(value);
        }
    }

    public final double getFromValue() {
        return (fromValue == null)? DEFAULT_FROM_VALUE : fromValue.get();
    }

    public final DoubleProperty fromValueProperty() {
        if (fromValue == null) {
            fromValue = new SimpleDoubleProperty(this, "fromValue", DEFAULT_FROM_VALUE);
        }
        return fromValue;
    }

    /**
     * Specifies the stop opacity value for this {@code DoubleTransition}.
     * <p>
     * It is not possible to change {@code toValue} of a running
     * {@code DoubleTransition}. If the value of {@code toValue} is changed for a
     * running {@code DoubleTransition}, the animation has to be stopped and
     * started again to pick up the new value.
     * 
     * @defaultValue {@code Double.NaN}
     */
    private DoubleProperty toValue;
    private static final double DEFAULT_TO_VALUE = Double.NaN;

    public final void setToValue(double value) {
        if ((toValue != null) || (!Double.isNaN(value))) {
            toValueProperty().set(value);
        }
    }

    public final double getToValue() {
        return (toValue == null)? DEFAULT_TO_VALUE : toValue.get();
    }

    public final DoubleProperty toValueProperty() {
        if (toValue == null) {
            toValue = new SimpleDoubleProperty(this, "toValue", DEFAULT_TO_VALUE);
        }
        return toValue;
    }

    /**
     * Specifies the incremented stop opacity value, from the start, of this
     * {@code DoubleTransition}.
     * <p>
     * It is not possible to change {@code byValue} of a running
     * {@code DoubleTransition}. If the value of {@code byValue} is changed for a
     * running {@code DoubleTransition}, the animation has to be stopped and
     * started again to pick up the new value.
     */
    private DoubleProperty byValue;
    private static final double DEFAULT_BY_VALUE = 0.0;

    public final void setByValue(double value) {
        if ((byValue != null) || (Math.abs(value - DEFAULT_BY_VALUE) > EPSILON)) {
            byValueProperty().set(value);
        }
    }

    public final double getByValue() {
        return (byValue == null)? DEFAULT_BY_VALUE : byValue.get();
    }

    public final DoubleProperty byValueProperty() {
        if (byValue == null) {
            byValue = new SimpleDoubleProperty(this, "byValue", DEFAULT_BY_VALUE);
        }
        return byValue;
    }

    /**
     * The constructor of {@code DoubleTransition}
     * 
     * @param duration
     *            The duration of the {@code DoubleTransition}
     * @param node
     *            The {@code node} which opacity will be animated
     */
    public DoubleTransition(Duration duration, DoubleProperty node) {
        setDuration(duration);
        setNode(node);
        setCycleDuration(duration);
    }

    /**
     * The constructor of {@code DoubleTransition}
     * 
     * @param duration
     *            The duration of the {@code DoubleTransition}
     */
    public DoubleTransition(Duration duration) {
        this(duration, null);
    }

    /**
     * The constructor of {@code DoubleTransition}
     */
    public DoubleTransition() {
        this(DEFAULT_DURATION, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void interpolate(double frac) {
        final double newOpacity = Math.max(0.0,
                Math.min(start + frac * delta, 1.0));
        node.set(newOpacity);
    }


   
        
    
}
