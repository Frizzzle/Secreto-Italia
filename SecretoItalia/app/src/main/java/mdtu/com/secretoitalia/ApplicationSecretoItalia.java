package mdtu.com.secretoitalia;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by koctyabondar on 9/17/15.
 */
public class ApplicationSecretoItalia  extends Application {
        @Override
        public void onCreate() {
            super.onCreate();

            Parse.enableLocalDatastore(this);
            Parse.initialize(this, "jhnvKQYa7hazPxrxykCnMM5LTVyyHygNbDb6JHvF", "KCFeVcvxhUUF5JTA7bWk5vntOQIEkWZdVFA7nudL");
        }

}
