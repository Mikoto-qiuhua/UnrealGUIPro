package org.qiuhua.UnrealGUIPro.videoPlay;

import com.daxton.unrealcore.application.UnrealCoreAPI;
import com.daxton.unrealcore.been.video.VideoPlayType;
import com.daxton.unrealcore.been.video.VideoPlayerBeen;
import com.daxton.unrealcore.display.been.module.ModuleData;
import com.daxton.unrealcore.display.been.module.control.ContainerModuleData;
import com.daxton.unrealcore.display.been.module.display.VideoModuleData;
import org.bukkit.entity.Player;
import org.qiuhua.UnrealGUIPro.Main;
import org.qiuhua.UnrealGUIPro.gui.GUIController;
import org.qiuhua.UnrealGUIPro.gui.UnrealGUIContainer;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class VideoPlay {


    public static void builder(Player player, VideoModuleData videoModuleData, String playType){
        //获取播放key
        String videoPlayer = videoModuleData.getVideoPlayer();
        //获取玩家当前界面
        UnrealGUIContainer unrealGUIContainer = GUIController.getUnrealGUIContainer(player);
        if(unrealGUIContainer != null){
            String url = unrealGUIContainer.getFileConfiguration().getString(videoModuleData.getFilePath() + ".Url");
            CopyOnWriteArrayList<String> videoPlayerList =  unrealGUIContainer.getVideoPlay();
            switch (playType) {
                case "播放":
                    play(player, videoPlayer, url);
                    if(!videoPlayerList.contains(videoPlayer)){
                        videoPlayerList.add(videoPlayer);
                    }
                    break;
                case "暂停":
                    pause(player, videoPlayer, url);
                    if(!videoPlayerList.contains(videoPlayer)){
                        videoPlayerList.add(videoPlayer);
                    }
                    break;
                case "结束":
                    stop(player, videoPlayer, url);
                    videoPlayerList.remove(videoPlayer);
                    break;
            }
        }
    }



    //播放一个指定连接的视频
    public static void play(Player player, String videoPlayer, String url){
        VideoPlayerBeen videoPlayerBeen = new VideoPlayerBeen(videoPlayer, url);
        videoPlayerBeen.setVideoPlayType(VideoPlayType.PLAY);
        UnrealCoreAPI.inst(player).getVideoHelper().setVideo(videoPlayerBeen);
        player.sendMessage("播放视频");
    }
    //暂停播放
    public static void pause(Player player, String videoPlayer, String url){
        VideoPlayerBeen videoPlayerBeen = new VideoPlayerBeen(videoPlayer, url);
        videoPlayerBeen.setVideoPlayType(VideoPlayType.PAUSE);
        UnrealCoreAPI.inst(player).getVideoHelper().setVideo(videoPlayerBeen);
        player.sendMessage("暂停视频");
    }
    //结束播放
    public static void stop(Player player, String videoPlayer, String url){
        VideoPlayerBeen videoPlayerBeen = new VideoPlayerBeen(videoPlayer, url);
        videoPlayerBeen.setVideoPlayType(VideoPlayType.STOP);
        UnrealCoreAPI.inst(player).getVideoHelper().setVideo(videoPlayerBeen);
        player.sendMessage("结束视频");
    }

    //视频组件查找
    public static VideoModuleData parseVideoPlay(Map<String, ModuleData> allModuleData, String afterDot){
        //包含-符号时进行分割
        if (afterDot.contains("-")){
            String[] subParts = afterDot.split("-"); // 使用"-"进行分割
            ContainerModuleData containerModule = null;
            ModuleData moduleData = null;
            for(String modelId : subParts){
                //这里肯定是第一次循环执行的在主要的data内拿
                if(containerModule == null){
                    containerModule = (ContainerModuleData) allModuleData.get(modelId);
                    continue;
                }
                //这里开始是第二次循环后的操作
                if(moduleData == null){
                    moduleData = containerModule.getModuleDataMap().get(modelId);
                }else{
                    ContainerModuleData a =  (ContainerModuleData) moduleData;
                    moduleData = a.getModuleDataMap().get(modelId);
                }
                //如果他是一个选项的话
                if(moduleData instanceof VideoModuleData module){
//                    Main.getMainPlugin().getLogger().info(module.getText());
                    return module;
                }
            }
        }
        ModuleData moduleData = allModuleData.get(afterDot);
        //如果他是一个视频的话
        if(moduleData instanceof VideoModuleData module){
//                    Main.getMainPlugin().getLogger().info(module.getText());
            return module;
        }
        return null;
    }

}
