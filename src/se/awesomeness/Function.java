package se.awesomeness;

public record Function(FunctionType type, double speed, double value) {

    public Point closestPoint(Point point) {
        Vector pointVector = new Vector(point);
        Vector adjustVector;

        switch (type) {
            case X_ACC_LIMIT -> {
                double maxTurn = 10 - 0.75 * Math.abs(speed);

                if (speed + value > 0){
                    adjustVector = new Vector(speed + value, pointVector.getDirection()).subtract(pointVector);
                    if (pointVector.getDirection() > maxTurn) {
                        return new Point(new Vector((speed + value), maxTurn).getFreeForm());
                    } else if (pointVector.getDirection() < -maxTurn) {
                        return new Point(new Vector((speed + value), -maxTurn).getFreeForm());
                    }
                }else if(speed + value < 0){
                    adjustVector = new Vector(speed + value, pointVector.getDirection()).negative().subtract(pointVector);
                    if (Tools.shortestAngle(pointVector.getDirection()-180) < -maxTurn) {
                        return new Point(new Vector((speed + value), -maxTurn).getFreeForm());
                    } else if (Tools.shortestAngle(pointVector.getDirection()-180) > maxTurn) {
                        return new Point(new Vector((speed + value), maxTurn).getFreeForm());
                    }
                }else{
                    return new Point();
                }
            }
            case Y_ACC_LIMIT -> {
                double adjustVectorMagnitude = Math.sin(Math.toRadians(pointVector.getDirection() - value * (10 - 0.75 * speed))) * pointVector.getMagnitude();
                double adjustVectorDirection = value * (10 - 0.75 * speed) - 90;
                adjustVector = new Vector(adjustVectorMagnitude, adjustVectorDirection);
            }
            default -> {
                adjustVector = new Vector(new Point(1,1));
                System.out.println("<ERROR> no type match in switch in Function.closestPoint. type: " + type.name());
            }
        }

        Vector test = pointVector.add(adjustVector);
        return test.getFreeForm();
    }

    public boolean withinFunction(Point point) {
        Vector pointVector = new Vector(point);
        double magnitude = pointVector.getMagnitude();
        double direction = pointVector.getDirection();
        double margin = 0.999999999;

        switch (type) {
            case X_ACC_LIMIT -> {
                if (direction > 90 || direction < -90){
                    magnitude *= -1;
                }
                if (speed+value < 0){
                    margin = 1/margin;
                }
                if (value > 0) {
                    return magnitude*margin <= (speed + value);
                }else if (value < 0){
                    return magnitude/margin >= speed + value;
                }else{
                    return Math.abs(magnitude)*margin < Math.abs(speed);
                }

            }
            case Y_ACC_LIMIT -> {
                if (direction > 90 || direction < -90){
                    direction = Tools.shortestAngle(180-direction);
                }
                return direction * 0.999999999 * value <= value * value * (10 - 0.75 * speed);

            }
            default -> {
                System.out.println("<ERROR> no type match in switch in Function.withinFunction. type: " + type.name());
                return false;
            }
        }
    }
}