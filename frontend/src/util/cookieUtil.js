class  CookiUtil {

    set(key, value) {
        let date = new Date(); 
        let validity = 30;
        date.setDate(date.getDate() + validity);
        // 쿠키 저장
        document.cookie = key + '=' + escape(value) + ';expires=' + date.toGMTString();
    }

     delete (key) {
        let date = new Date(); 
        let validity = -1;
        date.setDate(date.getDate() + validity);
        document.cookie =
            key + "=;expires=" + date.toGMTString();
    }

     get (name) {
        let nameOfCookie = name + "=";
        let x = 0;
        let endOfCookie = 0;
        while (x <= document.cookie.length) {
            let y = (x + nameOfCookie.length);
            if (document.cookie.substring(x, y) == nameOfCookie) {
                if ((endOfCookie = document.cookie.indexOf(";", y)) == -1)
                    endOfCookie = document.cookie.length;
                return unescape(document.cookie.substring(y, endOfCookie));
            }

            x = document.cookie.indexOf(" ", x) + 1;

            if (x == 0) break;
        }
        return null;
    }
}

export default new CookiUtil();