package com.lehms.messages.formDefinition;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


public enum FormElementType implements Serializable  {
	
	TextBox,
	DropDown,
	MultilineTextBox,
	RadioButtonList,
	CheckboxList,
	ImageDrawable,
	Date,
	Time,
	DateTime,
	Label,
	Checkbox,
	ImagePicker,
}
