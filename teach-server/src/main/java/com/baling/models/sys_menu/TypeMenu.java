package com.baling.models.sys_menu;

import com.baling.models.user.UserType;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "type_menu")
public class TypeMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @NotBlank
    @JoinColumn(name = "user_type_id")
    private UserType userType;

    @ManyToOne
    @NotBlank
    @JoinColumn(name = "sys_menu_id")
    private SysMenu sysMenu;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public SysMenu getSysMenu() {
        return sysMenu;
    }

    public void setSysMenu(SysMenu sysMenu) {
        this.sysMenu = sysMenu;
    }
}
