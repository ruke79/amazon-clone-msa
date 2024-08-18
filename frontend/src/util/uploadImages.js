import api from './api';

export const uploadImages = async (formData) => {
    const { data } = await api.post('/api/cloudinary', formData, {
        headers:{
            "content-type": "multipart/form-data",
        },
    })

    return data;
}