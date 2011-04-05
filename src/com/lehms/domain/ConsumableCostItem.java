package com.lehms.domain;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.content.Context;

import com.lehms.activerecord.ActiveRecordBase;
import com.lehms.activerecord.annotation.Column;
import com.lehms.activerecord.annotation.Table;

@Table(name = "ConsumableCostItem")
public class ConsumableCostItem extends ActiveRecordBase<ConsumableCostItem> {

	public ConsumableCostItem(Context context) {
		super(context);		
	}
	
	@Column(length = 255, name = "Identity")	
	public UUID Identity;
	
	@Column(length = 255, name = "CreatedBy")	
    public String CreatedBy;

	@Column(length = 0, name = "CreatedDate")	
	public Date CreatedDate;

	@Column(length = 255, name = "Subject")	
	public String Subject;

	@Column(length = 255, name = "ClientId")	
	public String ClientId;

	@Column(length = 4000, name = "Note")	
    public String Note;
    
	@Column(length = 255, name = "AttachmentId")	
    public UUID AttachmentId;	
	
	
	// REFERENCES
	
	//@Column(name = "Category")
	//public Category category;
	
	//public List<Item> items(Context context) {
	//	 return getMany(Item.class, "Category");
	//}
	
	// QUERY
	
	//public static void x(Context context)
	//{
	//	ProgressNote.query(context, ProgressNote.class, null, "WHERE", "ORDER BY" );
	//}
	
	//public static List<Item> getAll(Context context, Category category) {
	//	return Item.query(
	//		context, 
	//		Item.class, 
	//		new String[] { "Id", "Name"}, 
	//		"Category = " + category.getId(), 
	//		"Name ASC");
	//}

}
