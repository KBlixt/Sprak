package se.awesomeness;

public record Limit(LimitType type, double limit, double speed) {

    public Point closestPoint(Point point) {
        Vector pointVector = new Vector(point);
        Point closestPoint = new Point();

        switch (type) {
            case ACCELERATION_LIMIT -> {
                Vector closestPointVector = new Vector(speed + limit, pointVector.getDirection());
                if (speed + limit > 0){
                    closestPoint = closestPointVector.getFreeForm();
                }else if(speed + limit < 0){
                    closestPoint = closestPointVector.negative().getFreeForm();
                }
            }
            case TURN_LIMIT -> {
                double adjustVectorMagnitude = Math.sin(Math.toRadians(pointVector.getDirection() - limit)) * pointVector.getMagnitude();
                double adjustVectorDirection = limit - 90;
                closestPoint =  pointVector.add(new Vector(adjustVectorMagnitude, adjustVectorDirection)).getFreeForm();
            }
            default -> System.out.println("<ERROR> no type match in switch in Function.closestPoint. type: " + type.name());
        }
        return closestPoint;
    }

    public boolean withinFunction(Point point) {
        Vector pointVector = new Vector(point);
        double magnitude = pointVector.getMagnitude();
        double direction = pointVector.getDirection();
        double margin = 1.00000001;

        switch (type) {
            case ACCELERATION_LIMIT -> {
                if (magnitude < 0){
                    magnitude *= 1;
                    direction = Tools.oppositeAngle(direction);
                }
                //todo: invert vector if limit < 0, to avoid multiple boolean statements.

                double magnitudeLimit = Math.abs(speed+limit);
                boolean xIsPositiv = direction <= 90 && direction >= -90;
                if (limit>0){
                    boolean withinPositivX = magnitude <= magnitudeLimit*margin && xIsPositiv;
                    boolean outsideNegativeX = magnitude >= magnitudeLimit/margin && !xIsPositiv;
                    return withinPositivX || outsideNegativeX;
                }else if(limit < 0){
                    boolean outsidePositivX = magnitude >= magnitudeLimit/margin && xIsPositiv;
                    boolean insideNegativeX = magnitude <= magnitudeLimit*margin && !xIsPositiv;
                    return outsidePositivX || insideNegativeX;
                }else{
                    return Math.abs(magnitude)/margin < Math.abs(speed);
                }


            }
            case TURN_LIMIT -> {
                if (magnitude<0){
                    direction = Tools.oppositeAngle(direction);
                }
                //todo: invert vector if limit < 0, to avoid multiple boolean statements.

                if(limit > 0){
                    boolean underPositiveX = direction <= limit*margin && direction >= -90*margin;
                    boolean overNegativeX = direction >= 90/margin || direction <= Tools.oppositeAngle(limit)/margin;
                    return underPositiveX || overNegativeX;
                }else{
                    boolean overPositiveX = direction >= limit*margin && direction <= 90*margin;
                    boolean underNegativeX = direction <= -90/margin || direction >= Tools.oppositeAngle(limit)/margin;
                    return overPositiveX || underNegativeX;
                }

            }
            default -> {
                System.out.println("<ERROR> no type match in switch in Function.withinFunction. type: " + type.name());
                return false;
            }
        }
    }
}