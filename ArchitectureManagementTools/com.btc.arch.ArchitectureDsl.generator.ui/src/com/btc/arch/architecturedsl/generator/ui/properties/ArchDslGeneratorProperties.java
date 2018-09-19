package com.btc.arch.architecturedsl.generator.ui.properties;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

public class ArchDslGeneratorProperties extends PropertyPage {

	private static final int TEXT_FIELD_WIDTH = 75;

	private Map<String, Text> texts;
	private Map<String, String> defaults;

	/**
	 * Constructor for SamplePropertyPage.
	 */
	public ArchDslGeneratorProperties() {
		super();
		this.texts = new HashMap<String, Text>();
		this.defaults = new HashMap<String, String>();
	}

	private void addSection(Composite parent, String propertyName, String propertyTitle, String defaultValue) {
		Composite composite = createDefaultComposite(parent);

		// Label for target directory field
		Label targetDirectoryLabel = new Label(composite, SWT.NONE);
		targetDirectoryLabel.setText(propertyTitle);

		// Target directory text field
		Text targetDirectoryText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		targetDirectoryText.setLayoutData(gd);

		// Populate target directory text field
		try {
			String targetDirectory =
				((IResource) getElement()).getPersistentProperty(
					new QualifiedName("", propertyName));
			targetDirectoryText.setText((targetDirectory != null) ? targetDirectory : defaultValue);
		} catch (CoreException e) {
			targetDirectoryText.setText(defaultValue);
		}
		
		this.defaults.put(propertyName, defaultValue);
		this.texts.put(propertyName, targetDirectoryText);
	}

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);
		
		addSection(composite, "targetDir", "&Target Directory:", "D:\\");
		addSection(composite, "basepath", "&Base Path:", "");
		addSection(composite, "limitedTo", "&Limited To:", "");
		addSection(composite, "currentModule", "&Current Module:", "");
		addSection(composite, "releaseUnitName", "&Release Unit Name:", "");
		return composite;
	}

	private Composite createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}

	protected void performDefaults() {
		super.performDefaults();
		// Populate the target directory text field with the default value
		for (String propertyName : this.texts.keySet()) {
			this.texts.get(propertyName).setText(this.defaults.get(propertyName));
		}
	}
	
	public boolean performOk() {
		// store the value in the owner text field
		try {
			for (String propertyName : this.texts.keySet()) {
				((IResource) getElement()).setPersistentProperty(
						new QualifiedName("", propertyName),
						this.texts.get(propertyName).getText());
			}
		} catch (CoreException e) {
			return false;
		}
		return true;
	}

}