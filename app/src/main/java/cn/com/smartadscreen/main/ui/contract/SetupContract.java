package cn.com.smartadscreen.main.ui.contract;

public interface SetupContract {
    interface Presenter {
        boolean getValuesBySpManger(String key);

        void putValuesBySpManger(String key, Boolean vaules);
    }
}
