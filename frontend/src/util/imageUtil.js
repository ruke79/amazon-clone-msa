import axios from 'axios';
import { useEffect } from 'react';

export const instance = axios.create();
 

export const convertURLtoFile = async (url) => {
    const response = await fetch(url);
    const data = await response.blob();
    const ext = url.split(".").pop(); // url 구조에 맞게 수정할 것
    const filename = url.split("/").pop(); // url 구조에 맞게 수정할 것
    const metadata = { type: `image/${ext}` };
    return new File([data], filename, metadata);
  };

  export const fetchImageFromURL = (url) => {
    
    let promise = new Promise(async function (resolve, reject) {        
        const res = await convertURLtoFile(url);              
        resolve(res);                         
      }
    );

    return promise;         
  }

   
export const uploadImages = async (formData, config) => {    
    const response = await instance.post(`https://api.cloudinary.com/v1_1/
        ${process.env.REACT_APP_CLOUDINARY_NAME}/upload`,
         formData, config,
         {
        headers:{
            "content-type": "multipart/form-data",
        },        
    })

    return response.data;
}