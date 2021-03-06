package com.setecs.android.mpos.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.app.LoaderManager;

import com.setecs.android.mpos.adapters.MenuListAdapter;
import com.setecs.android.mpos.objects.MenuItem;
import com.setecs.android.mpos.provider.PersonnelRegister;

public class InventoryMenuItemsFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>{
	//OnProgressBarRefreshListener mCallback;
	
	public static String ARG_POSITION = "category_position";

	private int categoryId;
	private ListView listView;
	private LinearLayout btnMenuLView;	
	private int chosenId = 0;
	private Button btnAddMenu, btnUpdateMenu, btnDeleteMenu;
	private int beerIcons[] = { com.setecs.android.mpos.R.drawable.beer1, 
								com.setecs.android.mpos.R.drawable.beer2,
								com.setecs.android.mpos.R.drawable.beer3 
							  };
	private final String[] from = new String[]{
			 MenuItem.MenuColumns.MENU_ITEM_NAME,			  
			 MenuItem.MenuColumns.MENU_ITEM_PRICE,
			 MenuItem.MenuColumns.MENU_ITEM_DESCRIPTION
			};
	
	int [] to = new int[] {	com.setecs.android.mpos.R.id.menu_title,							
							com.setecs.android.mpos.R.id.menuitem_price,
							com.setecs.android.mpos.R.id.menu_description};
	//private MenuAdapter adapter;
	private MenuListAdapter cadapter;
	public void onCreate(Bundle state) {
		super.onCreate(state);
		
	}	
	public void btnAddMenuVisibility(){
		//btnLView.setVisibility(LinearLayout.VISIBLE);
		btnAddMenu.setVisibility(Button.VISIBLE);		
	}
	public void btnAddMenuGone(){
		btnAddMenu.setVisibility(Button.GONE);
	}
	public void enableAddMenuButton(){
		btnAddMenu.setEnabled(true);		
	}
	public void disableAddMenuButton(){
		btnAddMenu.setEnabled(false);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle b) {
		View view = inflater.inflate(com.setecs.android.mpos.R.layout.inventory_menu_list, container,
				false);
		 listView = (ListView) view.findViewById(com.setecs.android.mpos.R.id.menu_gridview);
		 btnMenuLView = (LinearLayout) view.findViewById(com.setecs.android.mpos.R.id.btn_menulayout);
		 btnAddMenu = (Button) view.findViewById(com.setecs.android.mpos.R.id.add_menu);
		 btnUpdateMenu = (Button) view.findViewById(com.setecs.android.mpos.R.id.btn_menuupdate);
		 btnDeleteMenu = (Button) view.findViewById(com.setecs.android.mpos.R.id.btn_menudelete);
		 btnAddMenu.setVisibility(Button.GONE);
		 btnMenuLView.setVisibility(LinearLayout.GONE);
		 Cursor cursor = getActivity().getContentResolver().query(
				 		 MenuItem.MenuColumns.CONTENT_URI, new String[] {MenuItem.MenuColumns.MENU_ITEM_ID, 
						 MenuItem.MenuColumns.MENU_ITEM_NAME, MenuItem.MenuColumns.MENU_ITEM_PRICE,
						 MenuItem.MenuColumns.MENU_ITEM_DESCRIPTION}, null, null, null);
		 getActivity().startManagingCursor(cursor);
		  
		   
		//GridView gridview = (GridView) view.findViewById(R.id.menu_gridview);
		
		
		//adapter = new MenuAdapter(getActivity(), beerIcons);
		   //(this, R.layout.list_entry, c, columns, to);
		cadapter = new MenuListAdapter(getActivity(),com.setecs.android.mpos.R.layout.inventory_menu_listrow, cursor, from, to);
		
		listView.setAdapter(cadapter);
		listView.setItemsCanFocus(false);
				
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// display menu details in menu details fragment
				chosenId = (int)id;
				//Toast.makeText(getActivity(), "You click on item!" + position + " have ID:" + id, Toast.LENGTH_SHORT).show();
				btnAddMenu.setVisibility(Button.GONE);
				btnMenuLView.setVisibility(LinearLayout.VISIBLE);
				cadapter.notifyDataSetChanged();
			}
		});

		// attach the add button
		//Button addMenuButton = (Button) view.findViewById(com.setecs.android.mpos.R.id.add_menu);
		btnAddMenu.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				addMenu();
			}
		});
		btnDeleteMenu.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				boolean result=false;
				result=deleteMenu(chosenId);
				//Toast.makeText(getActivity(), "Menu item deleted!" + result, Toast.LENGTH_SHORT).show();
			}
			
		});
		btnUpdateMenu.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String projection[]= new String[]{
						MenuItem.MenuColumns.MENU_ITEM_ID,
						MenuItem.MenuColumns.MENU_ITEM_NAME,
						MenuItem.MenuColumns.MENU_ITEM_PRICE,
						MenuItem.MenuColumns.MENU_ITEM_DESCRIPTION,
						MenuItem.MenuColumns.MENU_ITEM_CATEGORY_ID						
				};
				Cursor cr = getActivity().getContentResolver().query(MenuItem.MenuColumns.CONTENT_URI.buildUpon()
						.appendPath(String.valueOf(chosenId)).build(),
						projection, null, null, null);
				
				cr.moveToFirst();
				String menuId = (cr.getString(cr.getColumnIndex(MenuItem.MenuColumns.MENU_ITEM_ID)));
				String menuItemName=(cr.getString(cr.getColumnIndex(MenuItem.MenuColumns.MENU_ITEM_NAME)));
				String menuItemPrice = (cr.getString(cr.getColumnIndex(MenuItem.MenuColumns.MENU_ITEM_PRICE)));
				String menuCatID=(cr.getString(cr.getColumnIndex(MenuItem.MenuColumns.MENU_ITEM_CATEGORY_ID)));
				String menuDes=(cr.getString(cr.getColumnIndex(MenuItem.MenuColumns.MENU_ITEM_DESCRIPTION)));
				
				modifyMenu(menuId, menuItemName, menuItemPrice, menuCatID, menuDes);
			}
			
		});
		return view;
	}

	private void addMenu() {
		FragmentManager fm = getFragmentManager();
		if (fm.findFragmentByTag("dialog") == null) {

			AddMenuItemDialogFragment frag = AddMenuItemDialogFragment
					.newInstance();
			frag.setMenuCatId(categoryId);
			frag.show(fm, "dialog");
			
		}
	}
	private void modifyMenu(String menuID, String menuItemName, String menuItemPrice, String mCatID, String menuItemDes) {
		FragmentManager fm = getFragmentManager();
		if (fm.findFragmentByTag("dialog") == null) {

			ModifyMenuItemDialogFragment frag = ModifyMenuItemDialogFragment
					.newInstance(menuID, menuItemName, menuItemPrice, mCatID, menuItemDes);
			frag.setMenuCatId(categoryId);
			frag.show(fm, "dialog");
			
		}
	}
	private boolean deleteMenu(int IdToDelete){
		getActivity().getContentResolver().delete(MenuItem.MenuColumns.CONTENT_URI.buildUpon()
					 .appendPath(String.valueOf(IdToDelete)).build(), null, null);
		return true;
	}
	public void setCategoryId(int catId) {
		categoryId = catId;
	}
	public int getCategoryId() {
		return categoryId;
	}
	/*
	// Container Activity must implement this interface
	public interface OnProgressBarRefreshListener {
		public void onProgressBarRefresh(int positionUpdate);
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            	mCallback = (OnProgressBarRefreshListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnProgressBarRefreshListener");
        }
    }*/

	
		

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
}
