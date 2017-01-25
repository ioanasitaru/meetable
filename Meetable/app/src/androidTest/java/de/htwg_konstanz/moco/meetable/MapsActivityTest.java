package de.htwg_konstanz.moco.meetable;


import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.View;
import android.widget.Button;

/**
 * Created by bencikpeter on 25/01/2017.
 */

public class MapsActivityTest extends ActivityInstrumentationTestCase2<MapsActivity> {

    private MapsActivity mapsActivity;
    private Instrumentation instrumentation;
    private View refreshButton;
    private View chatButton;

    public MapsActivityTest(){
        super(MapsActivity.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();

        setActivityInitialTouchMode(true);

        instrumentation = getInstrumentation();
        mapsActivity = getActivity();
        refreshButton = mapsActivity.findViewById(R.id.button2);
        chatButton = mapsActivity.findViewById(R.id.button3);
    }

    public void testChatButtonExist(){
        View decorView = mapsActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView,chatButton);
    }
    public void testRefreshButtonExist(){
        View decorView = mapsActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView,refreshButton);
    }
    public void testRefreshButtonIsDisabled(){
        Button refresh = (Button) refreshButton;
        boolean enabled = refresh.isEnabled();
        assertEquals(enabled,true);
    }
    public void testCharButtonIsDisabled(){
        Button chat = (Button) chatButton;
        boolean enabled = chat.isEnabled();
        assertEquals(enabled,false);
    }


}
