class  CookiUtil {

    set(key, value, path='/', validity=30) {
        let date = new Date();         
        date.setDate(date.getDate() + validity);        
        document.cookie = key + '=' + encodeURI(value) + '; expires=' + date.toGMTString() + 'path=' + path;
    }

     delete (key, path='/') {
        let date = new Date(); 
        let validity = -1;
        date.setDate(date.getDate() + validity);
        document.cookie =
            key + '=; expires=' + date.toGMTString() + 'path=' + path;
        console.log(document.cookie);
    }

     get (name) {
        let cookieName = name + '=';
        let cookies = document.cookie;
        let start = cookies.indexOf(name);
        let value = '';
        if (start != -1) {
            start += cookieName.length;
            let end = cookies.indexOf(';', start);
            if (end === -1) end = cookies.length;
            value = cookies.substring(start, end);
        }      
        
        return decodeURI(value);
    }
}

export default new CookiUtil();