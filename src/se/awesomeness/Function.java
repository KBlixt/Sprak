package se.awesomeness;

public record Function(FunctionType type, double speed, double value) {

    public Point closestPoint(Point point) {
        Vector pointVector = new Vector(point);
        double adjustVectorMagnitude;
        double adjustVectorDirection;

        switch (type) {
            case X_ACC_LIMIT -> {
                adjustVectorMagnitude = (speed + value) - pointVector.getMagnitude();
                adjustVectorDirection = pointVector.getDirection();

                double maxTurn = 10 - 0.75 * speed;
                if (adjustVectorDirection > maxTurn) {
                    return new Point(new Vector((speed + value), maxTurn).getFreeForm());
                } else if (adjustVectorDirection < -maxTurn) {
                    return new Point(new Vector((speed + value), -maxTurn).getFreeForm());
                }
            }
            case Y_ACC_LIMIT -> {
                adjustVectorMagnitude = Math.sin(Math.toRadians(pointVector.getDirection() - value * (10 - 0.75 * speed))) * pointVector.getMagnitude();
                adjustVectorDirection = value * (10 - 0.75 * speed) - 90;
            }
            default -> {
                adjustVectorMagnitude = 1;
                adjustVectorDirection = 1;
                System.out.println("<ERROR> no type match in switch in Function.closestPoint. type: " + type.name());
            }
        }

        Vector adjustVector = new Vector(adjustVectorMagnitude, adjustVectorDirection);
        Vector test = pointVector.add(adjustVector);
        return test.getFreeForm();
    }

    public boolean withinFunction(Point point) {
        Vector pointVector = new Vector(point);
        double magnitude = Tools.round(pointVector.getMagnitude());
        double direction = Tools.round(pointVector.getDirection());

        switch (type) {
            case X_ACC_LIMIT -> {
                if (value >= 0) {
                    return magnitude * 0.999999999 <= speed + value;
                } else {
                    return magnitude * 1.000000001 >= speed + value;
                }
            }
            case Y_ACC_LIMIT -> {
                if (value >= 0) {
                    return direction * 0.999999999 <= value * (10 - 0.75 * speed);
                } else {
                    return direction * 1.000000001 >= value * (10 - 0.75 * speed);
                }
            }
            default -> {
                System.out.println("<ERROR> no type match in switch in Function.withinFunction. type: " + type.name());
                return false;
            }
        }
    }
}