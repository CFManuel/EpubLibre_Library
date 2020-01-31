/*
 * Copyright (c) 2020. Ladaga.
 * This file is part of EpubLibre_Library.
 *
 *     EpubLibre_Library is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     EpubLibre_Library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dmmop.vista.controllers;

import com.dmmop.files.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.prefs.Preferences;

public class ConfigLayout {
    private Stage dialogStage;

    @FXML
    private TextField tfHost;

    @FXML
    private TextField tfPort;

    @FXML
    private TextField tfUser;

    @FXML
    private TextField tfPass;

    @FXML
    private CheckBox cbUseProxy;

    @FXML
    private CheckBox cbUseProxyAut;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        this.dialogStage
                .getScene()
                .addEventHandler(
                        KeyEvent.KEY_PRESSED,
                        keyEvent -> {
                            if (keyEvent.getCode() == KeyCode.RIGHT) {
                                //nextBook();
                            } else if (keyEvent.getCode() == KeyCode.LEFT) {
                                //backBook();
                            }
                        });
    }

    @FXML
    private void initialize() {
        getProxyConfig();
        ChangeCB();
    }

    private void getProxyConfig() {
        Preferences prefs= Utils.getPrefIniEpub();
        cbUseProxy.setSelected( prefs.node("ProxyConfig").getBoolean("useProxy",true));
        tfHost.setText( prefs.node("ProxyConfig").get("Host",null));
        tfPort.setText(prefs.node("ProxyConfig").get("Port",null));
        cbUseProxyAut.setSelected(prefs.node("ProxyConfig").getBoolean("useProxyAut",true));
        tfUser.setText(prefs.node("ProxyConfig").get("User",null));
        tfPass.setText(prefs.node("ProxyConfig").get("Pass",null));
    }

    private void saveProxyConfig(){
        Utils.saveIniProxyConfig(cbUseProxy.isSelected(),tfHost.getText(),Integer.parseInt(tfPort.getText()),cbUseProxyAut.isSelected(),tfUser.getText(),tfPass.getText());
    }

    @FXML
    private void cancel() {
        dialogStage.close();
    }

    @FXML
    private void save() {
        saveProxyConfig();
        dialogStage.close();
    }

    @FXML
    private void onChangeCB(ActionEvent event) {
        ChangeCB();
    }

    private void ChangeCB(){
        if(cbUseProxy.isSelected()){
            tfHost.setDisable(false);
            tfPort.setDisable(false);
            cbUseProxyAut.setDisable(false);
            if(cbUseProxyAut.isSelected()){
                tfUser.setDisable(false);
                tfPass.setDisable(false);
            }else{
                tfUser.setDisable(true);
                tfPass.setDisable(true);
            }
        }else{
            tfHost.setDisable(true);
            tfPort.setDisable(true);
            cbUseProxyAut.setDisable(true);
            tfUser.setDisable(true);
            tfPass.setDisable(true);
        }
    }

}
