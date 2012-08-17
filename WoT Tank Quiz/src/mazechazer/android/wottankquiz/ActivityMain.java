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
    along with the WoT Tank Quiz.  If not, see <http://www.gnu.org/licenses/>.

    Diese Datei ist Teil des WoT Tank Quiz.

    Das WoT Tank Quiz ist Freie Software: Sie können es unter den Bedingungen
    der GNU General Public License, wie von der Free Software Foundation,
    Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren
    veröffentlichten Version, weiterverbreiten und/oder modifizieren.

    Das WoT Tank Quiz wird in der Hoffnung, dass es nützlich sein wird, aber
    OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite
    Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
    Siehe die GNU General Public License für weitere Details.

    Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
    Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>. */

package mazechazer.android.wottankquiz;

import java.util.Locale;

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

public class ActivityMain extends Activity {
	int level = 1;
//	final String notification = 
//		"Version 1.4\n" +
//		"•Fixed bug that the \"Last Notifications\"-dialog isn't shown\n" +
//		"\n" +
//		"Version 1.3\n" + 
//		"•Added new tanks of the WoT 7.3 update: KV-1, KV-2, T-150, ST-I, IS-4, IS-8, Type 62 and WZ-111\n" +
//		"•Fixed S-51 spelling mistake (\"SU-51\")\n" +
//		"•Fixed bug around Hummel/Grille\n" +
//		"\n" +
//		"Version 1.2\n" + 
//		"•Removed countdown bug when the pause dialog was cancelled with the back button\n" + 
//		"•Removed the bug that the pause dialog appears over the highscore dialog when activity is paused\n" + 
//		"•Added the \"Last Changes\"-notification\n" + 
//		"•Fixed the Bug that the highscore dialog displays the wrong score finally\n" + 
//		"\n" + 
//		"Version 1.1\n" + 
//		"•Improvements in the App Icon\n" + 
//		"•Fixed Bug that the highscore dialog displays the wrong score";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        String deviceLanguage = Locale.getDefault().getLanguage();
        String fontname;
        if (deviceLanguage.equals("ru")) {
        	fontname = "capitalist.ttf";
        } else {
        	fontname = "DESTROY_.ttf";
        }
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), fontname);
        ((Button)findViewById(R.id.buttonPlay)).setTypeface(myTypeface);
        ((Button)findViewById(R.id.buttonAbout)).setTypeface(myTypeface);
        ((Button)findViewById(R.id.buttonHighscore)).setTypeface(myTypeface);
        ((Button)findViewById(R.id.buttonLevel)).setTypeface(myTypeface);
        
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        PackageManager manager = this.getPackageManager();
        try {
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
	        if (! prefs.getString("notificationShowedVersion", "").equals(info.versionName)){
	        	SharedPreferences.Editor editor = prefs.edit();
	        	editor.putString("notificationShowedVersion", info.versionName);
	        	editor.commit();
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setTitle(getResources().getText(R.string.Changelog));
	        	builder.setMessage(getResources().getText(R.string.ChangelogText));
	        	builder.setNeutralButton(getResources().getText(R.string.Okay), new OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
	        		});
	        	builder.create().show();
	        }
        } catch (NameNotFoundException e) {}
    }
    public void Play(View view) {
    	Intent i = new Intent();
    	i.putExtra("level", level);
    	i.setClass(ActivityMain.this, ActivityQuizScreen.class);
    	startActivityForResult(i, 0);
    }
    public void showChangelog(View view){
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
    	    	((Button) findViewById(R.id.buttonLevel)).setText(items[item]);
    	    }
    	});
    	AlertDialog alert = builder.create();
    	alert.show();
    }
    public void buttonAboutClick(View view){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(R.string.AboutText);
    	builder.setNeutralButton("okay", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
    }

    public void showHighscore(View view){
    	startActivity(new Intent(ActivityMain.this, ActivityHighscore.class));
    }
}