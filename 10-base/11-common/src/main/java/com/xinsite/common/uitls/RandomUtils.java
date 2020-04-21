package com.xinsite.common.uitls;

import com.xinsite.common.uitls.lang.StringUtils;

import java.util.Random;

public class RandomUtils {
    private static String firstName = "赵,钱,孙,李,周,吴,郑,王,冯,陈,蒋,\n" +
            "    沈,杨,朱,秦,许,吕,施,张,孔,曹,金,魏,陶,姜,谢,\n" +
            "    苏,潘,范,彭,鲁,韦,马,丁,方,任,袁,柳,宋,\n" +
//            "    薛,雷,贺,倪,汤,殷,郁,单,洪,包,左,石,罗,关,\n" +
//            "    傅,康,伍,余,顾,孟,穆,韩,萧,尹,姚,毛,\n" +
//            "    瞿,庞,熊,项,祝,梁,向,龚,巩,芮,曾,\n" +
//            "    阮,季,贾,童,阎,习,耿,梅,钟,戚,高,\n" +
            "    夏,蔡,田,胡,林,霍,万,郭,苗,黄,董,江,\n" +
            "    邓,崔,程,陆,段,刘,叶,武,唐,徐,温,\n" +
            "    司马,上官,欧阳,夏侯,诸葛,东方,皇甫,尉迟,\n" +
            "    公孙,轩辕,令狐,宇文,长孙,慕容,司徒,司空";
    private static String lastName = "努,迪,立,林,维,吐,丽,新,涛,米,亚,克,湘,明,\n" +
            "    白,玉,代,孜,霖,霞,加,永,卿,约,小,刚,光,峰,春,基,木,国,娜,晓,兰,阿,伟,英,元,\n" +
            "    音,拉,亮,玲,木,兴,成,尔,远,东,华,旭,迪,吉,高,翠,莉,云,华,军,荣,柱,科,生,昊,\n" +
            "    耀,汤,胜,坚,仁,学,荣,延,成,庆,音,初,杰,宪,雄,久,培,祥,胜,梅,顺,涛,西,库,康,\n" +
            "    温,校,信,志,图,艾,赛,潘,多,振,伟,继,福,柯,雷,田,也,勇,乾,其,买,姚,杜,关,陈,\n" +
            "    静,宁,春,马,德,水,梦,晶,精,瑶,朗,语,日,月,星,河,飘,渺,星,空,如,萍,棕,影,南,北";

    private static String femaleName = "丽,湘,白,玉,霞,春,娜,兰,英,音,玲,高,翠,莉,云,初,梅,顺,涛,西,温,艾,赛,福,静,宁,春,水,梦,晶,瑶,如,萍";

    private static Random rand = new Random();

    /**
     * 自动生成姓名
     */
    public static String getRandomName(String Sex) {
        int namelength = 0;
        namelength = rand.nextInt(2) + 2;
        firstName = firstName.replace("\n", "");
        firstName = firstName.replace("\r", "");
        firstName = firstName.replace(" ", "");
        lastName = lastName.replace("\n", "");
        lastName = lastName.replace("\r", "");
        lastName = lastName.replace(" ", "");
        String name = "";
        String[] FirstName = firstName.split(",");
        String[] LastName = lastName.split(",");
        if (namelength == 2) {
            name = FirstName[rand.nextInt(FirstName.length)] + RandomUtils.getName(Sex, LastName);
        } else if (namelength == 3) {
            name = FirstName[rand.nextInt(FirstName.length)] + RandomUtils.getName(Sex, LastName) + RandomUtils.getName(Sex, LastName); //LastName[rand.nextInt(LastName.length)];
        }
        return name.replace(" ", "");
    }

    /**
     * 自动生成姓名
     */
    public static String getName(String Sex, String[] LastName) {
        String[] females = femaleName.split(",");
        if (Sex.equals("女")) {
            for (int i = 0; i < 100; i++) {
                String name = females[rand.nextInt(females.length)];
                if (!StringUtils.isEmpty(name)) {
                    return name;
                }
            }
        } else {
            for (int i = 0; i < 100; i++) {
                String name = LastName[rand.nextInt(LastName.length)];
                if (femaleName.indexOf(name) == -1 && !StringUtils.isEmpty(name)) {
                    return name;
                }
            }
        }
        return LastName[rand.nextInt(LastName.length)];
    }
}
