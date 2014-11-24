package adityash.imagesearch;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class AdvancedSettingsDialog extends DialogFragment implements OnItemSelectedListener {
	private AdvancedSettings settings;
	
	public interface AdvancedSettingsDialogListener {
        void onFinishEditDialog(AdvancedSettings settings);
    }
	
	public AdvancedSettingsDialog() {
	}
	
	public static AdvancedSettingsDialog newInstance(AdvancedSettings settings) {
		AdvancedSettingsDialog advancedSettingsDialog = new AdvancedSettingsDialog();
		Bundle args = new Bundle();
		args.putSerializable("settings", settings);
		advancedSettingsDialog.setArguments(args);
		return advancedSettingsDialog;
	}
	
	private void setupSpinner(Spinner spinner, String selection, int id) {
		SpinnerAdapter adapter = spinner.getAdapter();
		for(int i = 0; i < adapter.getCount(); i++) {
			if(adapter.getItem(i).equals(selection)) {
				spinner.setSelection(i);
				break;
			}
		}
		spinner.setTag(id);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle("Advanced Settings");
		
		View view = inflater.inflate(R.layout.advanced_settings, container);
		settings = (AdvancedSettings) getArguments().getSerializable("settings");
		
		Spinner spImageSize = (Spinner) view.findViewById(R.id.spImageSize);
		setupSpinner(spImageSize, settings.imageSize, R.id.spImageSize);
		spImageSize.setOnItemSelectedListener(this);
		
		Spinner spImageColor = (Spinner) view.findViewById(R.id.spImageColor);
		setupSpinner(spImageColor, settings.imageColor, R.id.spImageColor);
		spImageColor.setOnItemSelectedListener(this);
		
		Spinner spImageType = (Spinner) view.findViewById(R.id.spImageType);
		setupSpinner(spImageType, settings.imageType, R.id.spImageType);
		spImageType.setOnItemSelectedListener(this);
		
		EditText etSite = (EditText) view.findViewById(R.id.etSite);
		etSite.setText(settings.site);
		etSite.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				settings.site = s.toString();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		Button btnSave = (Button) view.findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveSettings();
			}
		});
		
		return view;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		Spinner spinner;
		int tag = 0;
		try {
			tag = Integer.valueOf(parent.getTag().toString());
		} catch (Exception e) {};
		switch(tag) {
		
			case R.id.spImageSize:
				spinner = (Spinner) parent;
				settings.imageSize = spinner.getSelectedItem().toString();
				break;
				
			case R.id.spImageColor:
				spinner = (Spinner) parent;
				settings.imageColor = spinner.getSelectedItem().toString();
				break;
				
			case R.id.spImageType:
				spinner = (Spinner) parent;
				settings.imageType = spinner.getSelectedItem().toString();
				break;
				
			default: break;
		
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}
	
	public void saveSettings() {
		AdvancedSettingsDialogListener l = (AdvancedSettingsDialogListener) getActivity();
		l.onFinishEditDialog(settings);
		dismiss();
	}
	
}
