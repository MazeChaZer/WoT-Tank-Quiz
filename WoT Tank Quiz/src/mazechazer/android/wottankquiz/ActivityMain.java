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

//import java.util.Locale;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.widget.TextView;

public class ActivityMain extends Activity {
    int level = 1;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button buttonPlay = (Button) findViewById(R.id.buttonPlay);
        Button buttonAbout = (Button) findViewById(R.id.buttonAbout);
        Button buttonHighscore = (Button) findViewById(R.id.buttonHighscore);
        Button buttonLevel = (Button) findViewById(R.id.buttonLevel);
        buttonPlay.setText(((String) getResources().getText(R.string.Play)).toUpperCase());
        buttonAbout.setText(((String) getResources().getText(R.string.About)).toUpperCase());
        buttonHighscore.setText(((String) getResources().getText(R.string.Highscore)).toUpperCase());
        buttonLevel.setText(((String) getResources().getText(R.string.Easy)).toUpperCase());
//        String deviceLanguage = Locale.getDefault().getLanguage();
//        String fontname;
//        if (deviceLanguage.equals("ru")) {
//            fontname = "capitalist.ttf";
//        } else {
//            fontname = "Destroy_new.ttf";
//        }
//        Typeface myTypeface = Typeface.createFromAsset(getAssets(), fontname);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "Destroy_new.ttf");
        buttonPlay.setTypeface(myTypeface);
        buttonAbout.setTypeface(myTypeface);
        buttonHighscore.setTypeface(myTypeface);
        buttonLevel.setTypeface(myTypeface);
        ((TextView) findViewById(R.id.textViewTankQuiz)).setTypeface(myTypeface);
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            if (! prefs.getString("notificationShowedVersion", "").equals(info.versionName)){
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("notificationShowedVersion", info.versionName);
                editor.commit();
                showChangelog();
            }
        } catch (NameNotFoundException e) {}
    }
    public void Play(View view) {
        Intent i = new Intent();
        i.putExtra("level", level);
        i.setClass(ActivityMain.this, ActivityQuizScreen.class);
        startActivityForResult(i, 0);
    }
    public void showChangelog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.Changelog);
        builder.setMessage(R.string.ChangelogText);
        builder.setNeutralButton(getResources().getText(R.string.Okay), new OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
            }
        });
        builder.create().show();
    }
    public void selectLevel(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.Level);
        final CharSequence items[] = {getResources().getText(R.string.Easy), getResources().getText(R.string.Medium), getResources().getText(R.string.Hard)};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                level = item + 1;
                ((Button) findViewById(R.id.buttonLevel)).setText(((String)items[item]).toUpperCase());
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void buttonAboutClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.About);
        builder.setMessage(R.string.AboutText);
        builder.setNegativeButton(R.string.Okay, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
           }
        });
        builder.setNeutralButton(R.string.Changelog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                    showChangelog();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showHighscore(View view){
        startActivity(new Intent(ActivityMain.this, ActivityHighscore.class));
    }
}