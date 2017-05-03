package se.kth.projectarbor.project_arbor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.Random;
/**
 * Created by Joseph on 5/3/2017.
 */

public class SnowFlake {
    private static final float ANGE_RANGE = 0.1f;
    private static final float HALF_ANGLE_RANGE = ANGE_RANGE / 2f;
    private static final float HALF_PI = (float) Math.PI / 2.25f;
    private static final float ANGLE_SEED = 25f;
    private static final float ANGLE_DIVISOR = 10000f;
    private static final float INCREMENT_LOWER = 4f;
    private static final float INCREMENT_UPPER = 8f;
    private static final float FLAKE_SIZE_LOWER = 4f;
    private static final float FLAKE_SIZE_UPPER = 6f;

    private final Random random;
    private final Point position;
    private float angle;
    private final float increment;
    private  final float flakeSize;
    private final Paint paint;

    public static SnowFlake create(int width, int height, Paint paint) {
        Random random = new Random();
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        Point position = new Point(x, y);
        float angle = random.nextFloat()*(ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE;
        float increment = random.nextFloat()*(INCREMENT_UPPER + INCREMENT_LOWER) +INCREMENT_UPPER;
        float flakeSize = random.nextFloat()*(FLAKE_SIZE_UPPER + FLAKE_SIZE_LOWER);
        return new SnowFlake(random, position, angle, increment, flakeSize, paint);
    }

    private SnowFlake(Random random, Point position, float angle, float increment, float flakeSize, Paint paint) {
        this.random = random;
        this.position = position;
        this.angle = angle;
        this.increment = increment;
        this.flakeSize = flakeSize;
        this.paint = paint;
    }

    private void move(int width, int height) {
        double x = position.x + (increment * Math.cos(angle));
        double y = position.y + (increment * Math.sin(angle));

        angle += (random.nextFloat()*(-ANGLE_SEED + ANGLE_SEED)) / ANGLE_DIVISOR;

        position.set((int) x, (int) y);

        if (!isInside(width, height)) {
            reset(width);
        }
    }

    private boolean isInside(int width, int height) {
        int x = position.x;
        int y = position.y;
        return x >= -flakeSize - 1 && x + flakeSize <= width && y >= -flakeSize - 1 && y - flakeSize < height;
    }

    private void reset(int width) {
        position.x = random.nextInt(width);
        position.y = (int) (-flakeSize - 1);
        angle = (random.nextFloat()* ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE;
    }

    public void draw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        move(width, height);
        canvas.drawCircle(position.x, position.y, flakeSize, paint);
    }
}

