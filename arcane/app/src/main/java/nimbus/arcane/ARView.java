package nimbus.arcane;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.opengl.Matrix;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by ntdat on 1/13/17.
 * Github : https://github.com/dat-ng/ar-location-based-android
 */
/**
 * Last Edited by Arnold Angelo 10/15/17
 */

public class ARView extends View{

    Context context;
    private float[] rotatedProjectionMatrix = new float[16];

    private Location currentLocation;
    private ARPoint destination;
    private List<ARPoint> arPoints;
    public static List<List<HashMap<String, String>>> routePoints;

    private int pointindex = 1;
    private boolean routing = true; //Boolean to determine between two AR Modes

    //Initializing the AR Points
    public ARView(Context context) {
        super(context);

        this.context = context;

        routePoints = MapFragment.routePoints;
        final List<HashMap<String,String>> pointList = routePoints.get(0);
        final int pointLength = pointList.size();
        destination = ARActivity.destinationARPoint;

        //Pass the Array of Locations into here to be rendered later
        arPoints = new ArrayList<ARPoint>() {{
            for (int j=0;j<pointLength;j++) {
                double lat=0;
                double lng=0;
                for (String key : pointList.get(j).keySet()) {
                    if (key=="lat") {
                        lat = Double.parseDouble(pointList.get(j).get(key));
                    } else if (key=="lng"){
                        lng = Double.parseDouble(pointList.get(j).get(key));
                    }
                }
                add(new ARPoint(lat,lng));
            }
            add(destination);
        }};

    }


    public void updateRotatedProjectionMatrix(float[] rotatedProjectionMatrix) {
        this.rotatedProjectionMatrix = rotatedProjectionMatrix;
        this.invalidate();
    }

    public void updateCurrentLocation(Location currentLocation){
        this.currentLocation = currentLocation;
        this.invalidate();
    }

    //Increment Index used for showing AR Points
    public void incrementIndex() {
        this.pointindex = this.pointindex + 1;
    }

    //Change/Setting Index used for showing AR Points
    public void setIndex(int index) {
        this.pointindex = index;
    }

    //Get the current Index
    public int getIndex() {
        return this.pointindex;
    }

    //Get the number of CheckPoints from user original position to target destination
    public int getARPointsSize() {
        return arPoints.size();
    }

    //Get the Current CheckPoint
    public ARPoint getARPoint() {
        return arPoints.get(pointindex);
    }

    //Set the AR Mode
    public void setType(boolean isRouting) {
        routing = isRouting;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentLocation == null) {
            return;
        }

        final int radius = 30;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(60);

        float[] currentLocationInECEF = LocationHelper.WSG84toECEF(currentLocation);

        //AR Routing Mode
        if (routing==true){
            if (pointindex < arPoints.size()) {
                float[] pointInECEF = LocationHelper.WSG84toECEF(arPoints.get(pointindex).getLocation());
                float[] pointInENU = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF, pointInECEF);

                float[] cameraCoordinateVector = new float[4];
                Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);

                // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
                // if z > 0, the point will display on the opposite
                if (cameraCoordinateVector[2] < 0) {
                    float x  = (0.5f + cameraCoordinateVector[0]/cameraCoordinateVector[3]) * canvas.getWidth();
                    float y = (0.5f - cameraCoordinateVector[1]/cameraCoordinateVector[3]) * canvas.getHeight();

                    canvas.drawCircle(x,y,radius+20,paint);
                    paint.setColor(Color.RED);
                    canvas.drawCircle(x, y, radius, paint);
                    canvas.drawText("Checkpoint "+(pointindex), x - (30*6), y-160, paint);
                    canvas.drawText("Distance : " + LocationHelper.distanceFromECEF(currentLocationInECEF,pointInECEF) + " m", x - (30 * 11), y - 80, paint);
                }
            }
            else {
                //Generate Text when all points have been reached
                paint.setColor(Color.RED);
                canvas.drawText("Arrived at Destination", canvas.getWidth()/4, canvas.getHeight()/2, paint);
            }
        }
        //AR Destination Target Mode
        else {
            float[] pointInECEF = LocationHelper.WSG84toECEF(destination.getLocation());
            float[] pointInENU = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF, pointInECEF);

            float[] cameraCoordinateVector = new float[4];
            Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);

            // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
            // if z > 0, the point will display on the opposite
            if (cameraCoordinateVector[2] < 0) {
                float x  = (0.5f + cameraCoordinateVector[0]/cameraCoordinateVector[3]) * canvas.getWidth();
                float y = (0.5f - cameraCoordinateVector[1]/cameraCoordinateVector[3]) * canvas.getHeight();

                canvas.drawCircle(x,y,radius+20,paint);
                paint.setColor(Color.RED);
                canvas.drawCircle(x, y, radius, paint);
                canvas.drawText(ARActivity.destinationUser, x - (30*(ARActivity.destinationUser.length()/2)), y-160, paint);
                canvas.drawText("Distance : " + LocationHelper.distanceFromECEF(currentLocationInECEF,pointInECEF) + " m", x - (30 * 11), y - 80, paint);
            }
        }
    }
}
