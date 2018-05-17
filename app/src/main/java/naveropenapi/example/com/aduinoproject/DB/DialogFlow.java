package naveropenapi.example.com.aduinoproject.DB;

import ai.api.AIConfiguration;
import ai.api.AIListener;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import naveropenapi.example.com.aduinoproject.R;

/**
 * Created by hyunungLim on 2018-05-14.
 */

public class DialogFlow implements AIListener{


    @Override
    public void onResult(AIResponse result) {

        final AIConfiguration config = new AIConfiguration(R.string.dial_token,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }
}
