package mazechazer.android.wottankquiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

public class ActivityLevelSelect extends Activity {
	int level = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.levelselect);
		
	}

	public void Play(View view) {
		Intent i = new Intent();

		i.putExtra("level", ((Spinner) findViewById(R.id.spinnerDifficulty)).getSelectedItemPosition());
		i.putExtra("limit", ((Spinner) findViewById(R.id.spinnerLimit)).getSelectedItemPosition());
		i.putExtra("wha", ((Spinner) findViewById(R.id.spinnerWhatToGuess)).getSelectedItemPosition());
		
		i.setClass(ActivityLevelSelect.this, ActivityQuizScreen.class);
		startActivityForResult(i, 0);
	}
}
