/*  Copyright 2012 Jonas Schürmann ©

    My Website: mazechazer.jimdo.com

    This file is part of  the WoT Tank Quiz.

    The WoT Tank Quiz is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    The WoT Tank Quiz is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the WoT Tank Quiz.  If not, see <http://www.gnu.org/licenses/>. */

package mazechazer.android.wottankquiz;

import java.util.ArrayList;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

public class ActivityHighscore extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highscores);
        loadNames();
    }
    private void loadNames(){
        String levelString;
        SharedPreferences saving = getSharedPreferences("highscore", MODE_PRIVATE);
        if (((RadioButton) findViewById(R.id.radioButtonEasyHighscore)).isChecked()){
            levelString = "Easy";
        } else if ((((RadioButton) findViewById(R.id.radioButtonMediumHighscore)).isChecked())){
            levelString = "Medium";
        } else {
            levelString = "Hard";
        }
        ArrayList<TextView> names = new ArrayList<TextView>();
        names.add((TextView) findViewById(R.id.TextViewName1));
        names.add((TextView) findViewById(R.id.TextViewName2));
        names.add((TextView) findViewById(R.id.TextViewName3));
        names.add((TextView) findViewById(R.id.TextViewName4));
        names.add((TextView) findViewById(R.id.TextViewName5));
        names.add((TextView) findViewById(R.id.TextViewName6));
        names.add((TextView) findViewById(R.id.TextViewName7));
        names.add((TextView) findViewById(R.id.TextViewName8));
        names.add((TextView) findViewById(R.id.TextViewName9));
        names.add((TextView) findViewById(R.id.TextViewName10));
        ArrayList<TextView> scores = new ArrayList<TextView>();
        scores.add((TextView) findViewById(R.id.TextViewScore1));
        scores.add((TextView) findViewById(R.id.TextViewScore2));
        scores.add((TextView) findViewById(R.id.TextViewScore3));
        scores.add((TextView) findViewById(R.id.TextViewScore4));
        scores.add((TextView) findViewById(R.id.TextViewScore5));
        scores.add((TextView) findViewById(R.id.TextViewScore6));
        scores.add((TextView) findViewById(R.id.TextViewScore7));
        scores.add((TextView) findViewById(R.id.TextViewScore8));
        scores.add((TextView) findViewById(R.id.TextViewScore9));
        scores.add((TextView) findViewById(R.id.TextViewScore10));
        for (int i = 1; i <= 10; i++){
            if (! saving.getString(levelString + "Name" + Integer.toString(i), "").equals("")){
                names.get(i - 1).setText(saving.getString(levelString + "Name" + Integer.toString(i), ""));
                scores.get(i - 1).setText(Integer.toString(saving.getInt(levelString + "Score" + Integer.toString(i), 0)));
            } else {
                names.get(i - 1).setText("");
                scores.get(i - 1).setText("");
            }
        }
    }
    public void onRadioButtonClick(View view){
        loadNames();
    }
}
