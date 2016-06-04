package lltan.jikexueyuan.com.lego.bean;

import android.view.View;

import java.io.Serializable;

/**
 * Created by legoer
 */
public class ShoppingCart extends Wares implements Serializable {

//    private int cartId = 0;
    private int count;
    private boolean isChecked = false;
    private boolean isNumberAddSubVisible = false;

    private Float totalPrice = Float.valueOf(0);

    public boolean isNumberAddSubVisible() {
        return isNumberAddSubVisible;
    }

    public void setIsNumberAddSubVisible(boolean isNumberAddSubVisible) {
        this.isNumberAddSubVisible = isNumberAddSubVisible;
    }

    public Float getTotalPrice() {
        if(count == 0){
            count = 1;
        }
        totalPrice = getPrice() * count;
        return totalPrice;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

//    public int getCartId() {
//        return cartId;
//    }
//
//    public void setCartId(int cartId) {
//        this.cartId = cartId;
//    }
}
