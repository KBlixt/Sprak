package se.awesomeness;

public record Limit(LimitType type, double limit) {

    public Point closestPoint(Point point) {
        Vector pointVector = new Vector(point);
        Point closestPoint = new Point();

        switch (type) {
            case ACCELERATE_LIMIT, DECELERATE_LIMIT ->{
                Vector closestPointVector;
                double direction = pointVector.getDirection();
                if (direction <= 90 && direction >= -90 ){
                    closestPointVector = new Vector(limit, pointVector.getDirection());
                }else{
                    closestPointVector = new Vector(limit, pointVector.negative().getDirection());
                }
                closestPoint = closestPointVector.getFreeForm();
            }
            case TURN_RIGHT_LIMIT, TURN_LEFT_LIMIT -> {
                double adjustVectorMagnitude = Math.sin(Math.toRadians(pointVector.getDirection() - limit)) * pointVector.getMagnitude();
                double adjustVectorDirection = limit - 90;
                closestPoint =  pointVector.add(new Vector(adjustVectorMagnitude, adjustVectorDirection)).getFreeForm();
            }
            default ->
                    System.out.println("<ERROR> no type match in switch in Function.closestPoint. type: " + type.name());
        }
        return closestPoint;
    }

    public boolean withinLimit(Point point) {
        Vector pointVector = new Vector(point);
        double magnitude = pointVector.getMagnitude();
        double direction = pointVector.getDirection();
        if (magnitude < 0) {
            magnitude *= -1;
            direction = Tools.oppositeAngle(direction);
        }
        boolean xIsPositiv = direction <= 90 && direction >= -90;

        double margin = 0.000000001;
        boolean withinLimit = false;
        switch (type) {
            case ACCELERATE_LIMIT -> {
                if (limit >= 0 ^ xIsPositiv){
                    withinLimit = limit >= 0;
                }else{
                    boolean withinLimitPosX = magnitude <= Math.abs(limit) + margin && xIsPositiv;
                    boolean withinLimitNegX = magnitude >= Math.abs(limit) - margin && !xIsPositiv;
                    withinLimit = withinLimitPosX || withinLimitNegX;
                }
            }
            case DECELERATE_LIMIT -> {
                if (limit >= 0 ^ xIsPositiv){
                    withinLimit = limit <= 0;
                }else{
                    boolean withinLimitPosX = magnitude >= Math.abs(limit) - margin && xIsPositiv;
                    boolean withinLimitNegX = magnitude <= Math.abs(limit) + margin && !xIsPositiv;
                    withinLimit = withinLimitPosX || withinLimitNegX;
                }
            }
            case TURN_LEFT_LIMIT ->{
                if (xIsPositiv){
                    withinLimit = direction <= limit + margin;
                }else{
                    withinLimit = direction <= Tools.oppositeAngle(limit) + margin || direction >= 90 - margin;
                }
            }
            case TURN_RIGHT_LIMIT -> {
                if (xIsPositiv) {
                    withinLimit = direction >= limit - margin;
                } else {
                    withinLimit = direction >= Tools.oppositeAngle(limit) - margin || direction <= -90 + margin;
                }
            }
            default ->
                    System.out.println("<ERROR> no type match in switch in Function.withinFunction. type: " + type.name());
        }
        return withinLimit;
    }
}