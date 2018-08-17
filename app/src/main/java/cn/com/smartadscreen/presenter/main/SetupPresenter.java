package cn.com.smartadscreen.presenter.main;

import android.app.Activity;

import cn.com.smartadscreen.main.ui.contract.SetupContract;
import cn.com.smartadscreen.model.sp.SPManager;

public class SetupPresenter implements SetupContract.Presenter {
    private final Activity SetupView;


    public SetupPresenter(Activity activity) {
        SetupView = activity;
    }

    @Override
    public boolean getValuesBySpManger(String key) {
        if (key == SPManager.KEY_ENABLED_HARDWARE_KEY) {
           return SPManager.getManager().getBoolean(SPManager.KEY_ENABLED_HARDWARE_KEY, true);
        } else if (key == SPManager.KEY_ENABLED_TOAST_DOWNLOAD_INFO) {
          return   SPManager.getManager().getBoolean(SPManager.KEY_ENABLED_TOAST_DOWNLOAD_INFO, false);
        } else if (key == SPManager.KEY_APP_USE_CROSSWALK) {
           return SPManager.getManager().getBoolean(SPManager.KEY_APP_USE_CROSSWALK, false);
        } else if (key == SPManager.KEY_ENABLED_TOUCH_EVENT) {
           return SPManager.getManager().getBoolean(SPManager.KEY_ENABLED_TOUCH_EVENT, false);
        }

        return false;
    }

    @Override
    public void putValuesBySpManger(String key, Boolean vaules) {
        SPManager.getManager().put(key, vaules);
    }
}
