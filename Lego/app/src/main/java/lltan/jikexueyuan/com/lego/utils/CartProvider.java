package lltan.jikexueyuan.com.lego.utils;

import android.content.Context;
import android.util.SparseArray;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import lltan.jikexueyuan.com.lego.bean.ShoppingCart;
import lltan.jikexueyuan.com.lego.bean.Wares;


/**
 * Created by legoer
 */
public class CartProvider {



    public static final String CART_JSON="cart_json";

//    private static int index = 0;

    private SparseArray<ShoppingCart> datas =null;
    private LinkedList<ShoppingCart> shoppingDatas = null;


    private  Context mContext;


    public CartProvider(Context context){

        mContext = context;
        datas = new SparseArray<>(10);
        shoppingDatas = new LinkedList<>();
        listToSparse();
//        System.out.println(ShoppingCart.cartId);
    }



    public void put(ShoppingCart cart){


        ShoppingCart temp =  datas.get(cart.getId().intValue());

        if(temp !=null){
            temp.setCount(temp.getCount() + 1);
        }
        else{
            temp = cart;
            temp.setCount(1);
            shoppingDatas.push(cart);
        }

        datas.put(cart.getId().intValue(), temp);
        commit();

    }


    public void put(Wares wares){


        ShoppingCart cart = convertData(wares);
        put(cart);
    }

    public void update(ShoppingCart cart) {

        datas.put(cart.getId().intValue(),cart);
        commit();
    }

    public void delete(ShoppingCart cart){
        datas.delete(cart.getId().intValue());
        shoppingDatas.remove(cart);
        commit();
    }

    public List<ShoppingCart> getAll(){

        return  getDataFromLocal();
    }


    public void commit(){


        List<ShoppingCart> carts = sparseToList();

        PreferencesUtils.putString(mContext,CART_JSON,JSONUtil.toJSON(carts));

    }


    private List<ShoppingCart> sparseToList(){


        int size = shoppingDatas.size();

        System.out.println(size);
        List<ShoppingCart> list = new ArrayList<>(size);
        for (int i= 0;i<size;i++){
            list.add(shoppingDatas.get(i));
        }
        return list;

    }



    private void listToSparse(){

        List<ShoppingCart> carts =  getDataFromLocal();

        if(carts!=null && carts.size()>0){

            for (ShoppingCart cart: carts) {

                datas.put(cart.getId().intValue(), cart);
                shoppingDatas.push(cart);
            }
        }

    }



    public  List<ShoppingCart> getDataFromLocal(){

        String json = PreferencesUtils.getString(mContext,CART_JSON);
        System.out.println(json);
        List<ShoppingCart> carts =null;
        if(json !=null ){

            carts = JSONUtil.fromJson(json,new TypeToken<List<ShoppingCart>>(){}.getType());

        }

        return  carts;

    }


    public ShoppingCart convertData(Wares item){

        ShoppingCart cart = new ShoppingCart();

        cart.setId(item.getId());
        cart.setDescription(item.getDescription());
        cart.setImgUrl(item.getImgUrl());
        cart.setName(item.getName());
        cart.setPrice(item.getPrice());

        return cart;
    }



}
