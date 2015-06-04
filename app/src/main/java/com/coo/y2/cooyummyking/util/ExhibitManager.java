package com.coo.y2.cooyummyking.util;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Y2 on 2015-06-04.
 * ToolDetailEditorFragment, ~PageFragment, ~PhotoFragment에서 편집 이미지 저장중일 때 보여줄 이미지를 관리하기 위한 클래스
 * scriber 불러서 start, finish save process 하고
 * getImitation으로 이미지 있음 쓰고 없음 새로 부르는게 전부.
 *
 * +@ 지금 생각해보니 recycle 해도 계속 보여진다는것은 bitmap을 따로 만들어둔다는 것 같은데, 그렇다면 큰 이미지를 사용하면 메모리 누출 엄청날지도..
 * 한 파일이기에 여러곳에 써도 하나 쓰는 꼴인게 아닐지도.. 확인해야.(메모리 소모량으로 봐도 되고, 천천히 생각)
 */
public class ExhibitManager {
    private static ExhibitManager sManager;
    private HashMap<Integer, Exhibit> exhibits = new HashMap<>();

    // 동기화가 필요한가..? 저장되는 과정이 백그라운드인게 관건인데, 어차피 저장 끝과 시작 때 메인스레드에서 state 변경하니 상관없지않을까.
    // state가 바뀌는 그 자체의 과정이 길어서 사실 이미 state 바뀐건데 안바뀐줄알고 image를 가져다쓴다거나 하는 처리를 하는게 문제인데.
    // state와 image가 동시에 변경되니 안써도 괜찮겠다!!
    //// 이너클래스는 아우터클래스 크기를 무조건 늘리니 사실 밖으로 빼는게 나음.. 근데 그러면 이름에 lab이 붙어 더 길어져서 그지같네 간단한건데 두 파일로 나뉘면 좀 지저분하기도 하고.
    public class Exhibit {
        private int index;
        private Bitmap image;
        private boolean isInSaveProcess = false;
        private boolean wait = false;

        public Exhibit(int index) {
            this.index = index;
        }

        public void startSaveProcess(Bitmap image) {
            isInSaveProcess = true;
            this.image = image;
        }

        public void finishSaveProcess() {
            isInSaveProcess = false;
            if (image != null) image.recycle();
            exhibits.remove(index);

            if (wait) Thread.interrupted();
        }

        public Bitmap getImage() {
            return image;
        }

        public boolean isInSaveProcess() {
            return isInSaveProcess;
        }

        public void waitIfSaveProcess() {
            if (isInSaveProcess) {
                try {
                    Log.i("CYMK", "wait for " + index + " save process");
                    Thread.sleep(7000);
                } catch(InterruptedException e) {
                    Log.i("CYMK", index + " save process got finish");
                }
                wait = true;
            }
        }
    }

    /**
     * Get Exhibit instance for change state of save process.
     * Should be called in save processor
     * Otherwise it'll make unnecessary Exhibit instance.
     * @param index index of item
     * @return Exhibit instance
     */
    public static Exhibit scriber(int index) {
        if (sManager == null) sManager = new ExhibitManager();


        Exhibit exhibit;
        if ((exhibit = sManager.exhibits.get(index)) == null) {
            exhibit = sManager.new Exhibit(index); // 이런게 있었다니!! // Outer 클래스의 static 메서드에서 InnerClass의 인스턴스를 생성하는 법
            sManager.exhibits.put(index, exhibit);
        }
        return exhibit;
    }

    // 사용하기가 번거로우므로 쓰지 않는다.
//    /**
//     * Get Exhibit instance for check state of save process.
//     * Should be called when check state is needed.
//     * By doing so this will save memory.
//     * @param index index of item
//     * @return If save never started -> return null. then do the normal -load new file- process.
//     * Otherwise return Exhibit instance
//     */
//    public static Exhibit checker(int index) {
//        if (sManager == null) return null;
//
//        return sManager.exhibits.get(index);
//    }

    /**
     * Get temp image while save process is ongoing
     * @param index
     * @return return last edited image IF save process is ongoing.
     * Otherwise return null
     */
    public static Bitmap getReplica(int index) {
        if (sManager == null) return null;

        Exhibit exhibit = sManager.exhibits.get(index);
        if (exhibit == null) return null;
        if (!exhibit.isInSaveProcess()) return null;

        return exhibit.getImage();
    }

    public static ArrayList<Exhibit> getAllExhibit() {
        if (sManager == null) return null;

        ArrayList<Exhibit> exhibits = new ArrayList<>();
        exhibits.addAll(sManager.exhibits.values());

        return exhibits;
    }
}
