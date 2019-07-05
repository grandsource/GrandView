package grandsource.grandview;

import android.content.Intent;

public interface GrandResult {
    void getResult(int requestCode, int resultCode, Intent data);
}