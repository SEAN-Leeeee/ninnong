import api from '@/axios.js'

export async function uploadFile(file) {
    const formData = new FormData()
    formData.append('file', file)

    const response = await api.post('/files', formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    })
    return response.data
}

export async function uploadMultipleFiles(files) {
    const uploadPromises = files.map((file) => uploadFile(file))
    return Promise.all(uploadPromises)
}
