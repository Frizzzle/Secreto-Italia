package mdtu.com.secretoitalia.Wrappers;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;

import mdtu.com.secretoitalia.Adapters.BagAdapter;
import mdtu.com.secretoitalia.BagActivity;

/**
 * Created by koctyabondar on 9/16/15.
 */
public class SearchDialogThread  extends Thread {




        private BagActivity search;
        private ProgressDialog pd;
        private int responseCode;

        public SearchDialogThread(BagActivity search,ProgressDialog pd) {
            this.search = search;
            this.pd = pd;
        }

        @Override
        public void run() {
            responseCode = search.check();
            handler.sendEmptyMessage(0);
        }

        private Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                pd.dismiss();
                search.showAlert(responseCode);
            }
        };

}
