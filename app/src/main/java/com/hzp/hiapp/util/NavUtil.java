package com.hzp.hiapp.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.AssetManager;
import android.view.Menu;
import android.view.MenuItem;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.DialogFragmentNavigator;
import androidx.navigation.fragment.FragmentNavigator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hzp.hiapp.R;
import com.hzp.hiapp.model.BottomBar;
import com.hzp.hiapp.model.Destination;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class NavUtil {

    public static String parseFile(Context context, String filename) {
        AssetManager assetManager = context.getAssets();

        try {
            InputStream inputStream = assetManager.open(filename);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            inputStream.close();
            bufferedReader.close();

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 构建NavGraph
     * 自定义HiFragmentNavigator
     * 不会导致Fragment切换生命周期重启
     * @param activity
     * @param childFragmentManager
     * @param controller
     * @param containerId
     */
    public static void builderNavGraph(FragmentActivity activity, FragmentManager childFragmentManager, NavController controller, int containerId) {
        HashMap<String, Destination> destinations = JSON.parseObject(parseFile(activity, "destination.json"),
                new TypeReference<HashMap<String, Destination>>() {}.getType());
        Iterator<Destination> iterator = destinations.values().iterator();
        NavigatorProvider provider = controller.getNavigatorProvider();
        NavGraphNavigator graphNavigator = provider.getNavigator(NavGraphNavigator.class);
        NavGraph navGraph = new NavGraph(graphNavigator);

        HiFragmentNavigator hiFragmentNavigator = new HiFragmentNavigator(activity, childFragmentManager, containerId);
        provider.addNavigator(hiFragmentNavigator);

        while (iterator.hasNext()) {
            Destination destination = iterator.next();
            if (destination.destType.equals("activity")) {
                ActivityNavigator navigator = provider.getNavigator(ActivityNavigator.class);
                ActivityNavigator.Destination node = navigator.createDestination();
                node.setId(destination.id);
                node.setComponentName(new ComponentName(activity.getPackageName(), destination.clazzName));
                navGraph.addDestination(node);
            } else if (destination.destType.equals("fragment")) {
                HiFragmentNavigator.Destination node = hiFragmentNavigator.createDestination();
                node.setId(destination.id);
                node.setClassName(destination.clazzName);
                navGraph.addDestination(node);
            } else if (destination.destType.equals("dialog")) {
                DialogFragmentNavigator navigator = provider.getNavigator(DialogFragmentNavigator.class);
                DialogFragmentNavigator.Destination node = navigator.createDestination();
                node.setId(destination.id);
                node.setClassName(destination.clazzName);
                navGraph.addDestination(node);
            }

            if (destination.asStarter) {
                navGraph.setStartDestination(destination.id);
            }
        }

        controller.setGraph(navGraph);
    }

    /**
     * 构建NavGraph
     * @param activity
     * @param controller
     * @param containerId
     */
    public static void builderNavGraph(FragmentActivity activity, NavController controller, int containerId) {
        HashMap<String, Destination> destinations = JSON.parseObject(parseFile(activity, "destination.json"),
                new TypeReference<HashMap<String, Destination>>() {}.getType());
        Iterator<Destination> iterator = destinations.values().iterator();
        NavigatorProvider provider = controller.getNavigatorProvider();
        NavGraphNavigator graphNavigator = provider.getNavigator(NavGraphNavigator.class);
        NavGraph navGraph = new NavGraph(graphNavigator);

        while (iterator.hasNext()) {
            Destination destination = iterator.next();
            if (destination.destType.equals("activity")) {
                ActivityNavigator navigator = provider.getNavigator(ActivityNavigator.class);
                ActivityNavigator.Destination node = navigator.createDestination();
                node.setId(destination.id);
                node.setComponentName(new ComponentName(activity.getPackageName(), destination.clazzName));
                navGraph.addDestination(node);
            } else if (destination.destType.equals("fragment")) {
                FragmentNavigator navigator = provider.getNavigator(FragmentNavigator.class);
                FragmentNavigator.Destination node = navigator.createDestination();
                node.setId(destination.id);
                node.setClassName(destination.clazzName);
                navGraph.addDestination(node);
            } else if (destination.destType.equals("dialog")) {
                DialogFragmentNavigator navigator = provider.getNavigator(DialogFragmentNavigator.class);
                DialogFragmentNavigator.Destination node = navigator.createDestination();
                node.setId(destination.id);
                node.setClassName(destination.clazzName);
                navGraph.addDestination(node);
            }

            if (destination.asStarter) {
                navGraph.setStartDestination(destination.id);
            }
        }

        controller.setGraph(navGraph);
    }

    /**
     * 构建BottomNavigationView
     * @param navView
     */
    public static void builderBottomBar(BottomNavigationView navView){
        HashMap<String, Destination> destinations = JSON.parseObject(parseFile(navView.getContext(), "destination.json"),
                new TypeReference<HashMap<String, Destination>>() {}.getType());

        String content = parseFile(navView.getContext(), "main_tabs_config.json");
        BottomBar bottomBar = JSON.parseObject(content, BottomBar.class);

        List<BottomBar.Tab> tabs = bottomBar.getTabs();
        Menu menu = navView.getMenu();
        for (BottomBar.Tab tab : tabs) {
            if(!tab.isEnable())continue;
            Destination destination = destinations.get(tab.getPageUrl());
            if(destination!=null){
                MenuItem menuItem = menu.add(0, destination.id, tab.getIndex(), tab.getTitle());

                //TODO 待完善
                menuItem.setIcon(R.drawable.ic_home_black_24dp);


            }
        }
    }
}
