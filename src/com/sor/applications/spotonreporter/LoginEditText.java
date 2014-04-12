package com.sor.applications.spotonreporter;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

public class LoginEditText extends EditText {
	public LoginEditText(Context context) {
		super(context);
		this.setSingleLine();
		this.setImeOptions(EditorInfo.IME_ACTION_DONE);
		this.setImeActionLabel("Done", EditorInfo.IME_ACTION_DONE);
		this.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}
}
