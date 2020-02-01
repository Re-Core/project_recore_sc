package com.recore.projectrecoresc.Interface;

import com.recore.projectrecoresc.Model.Camera;

import java.util.List;

public interface IOnLoadLocationListener {

    void OnLoadLocationSuccess(List<Camera>latLngs);
    void OnLoadLocationFailed(String msg);

}
