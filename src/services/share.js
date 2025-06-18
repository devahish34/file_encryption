import axios from 'axios';

const shareService = {
  shareFile: (shareData) => {
    return axios.post('/api/share', shareData);
  },
  
  generateShareLink: (shareId) => {
    return `${window.location.origin}/shared/${shareId}`;
  }
};

export default shareService;