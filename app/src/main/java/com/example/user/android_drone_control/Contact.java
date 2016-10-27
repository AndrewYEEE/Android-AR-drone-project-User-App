package com.example.user.android_drone_control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/9/8.
 */
/*此java是為了RecyclerView元件所建立*/
public class Contact {
    private String name;

    public Contact() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<Contact> generateSampleList(){
        List<Contact> list = new ArrayList<>();
        for(int i=0; i < 30; i++){
            Contact contact = new Contact();
            contact.setName("Name - " + i);
            list.add(contact);
        }
        return list;
    }
}
