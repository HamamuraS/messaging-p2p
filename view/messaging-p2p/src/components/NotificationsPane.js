import React from 'react';
import { Dialog, DialogTitle, DialogContent, Card, CardContent, Typography, Button, Grid, IconButton, Box } from "@mui/material";
import { CheckCircle, Cancel, Close } from '@mui/icons-material';
import updateFriendshipRequestWithAccepted from '../utils/updateFriendshipRequestWithAccepted';
import NotificationSnackbar from '../utils/successSnackbar';

const NotificationsPane = ({ open, setOpen, notifications, setNotifications, setChannels }) => {

  const [snackBarOpen, setSnackBarOpen] = React.useState(false);
  
  const handleClose = () => {
    setOpen(false);
  };
  
  const processNotificationWithValue = async (accepted, requestId) => {
    
    const createdChannel = await updateFriendshipRequestWithAccepted(accepted, requestId);
    
    if (createdChannel && accepted) {
      setSnackBarOpen(true);
      setChannels((channels) => [...channels, createdChannel]);
    }
    setNotifications(notifications.filter((n) => n.id !== requestId));
    
  };

  const displayNotifications = () => {
    return notifications.map((notification) => (
      <Card key={notification.id} style={{ marginBottom: '15px', borderRadius: '10px', boxShadow: '0px 3px 6px rgba(0, 0, 0, 0.1)' }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            {notification.messageType === "FRIENDSHIP_REQUEST" ? "Friendship Request" : "Notification"}
          </Typography>
          <Grid container alignItems="center" spacing={2}>
            <Grid item xs={8}>
              <Typography variant="body1" color="textPrimary">
                From: <strong>{notification.senderName}</strong>
              </Typography>
              <Typography variant="body1" color="textPrimary">
                To: <strong>{notification.receiverName}</strong>
              </Typography>
              <Typography variant="body2" color="textSecondary" style={{ marginTop: '8px' }}>
                Date: {new Date(notification.timestamp).toLocaleString()}
              </Typography>
            </Grid>
            <Grid item xs={4} container justifyContent="flex-end" spacing={1}>
              <Grid item>
                <Button
                  variant="outlined"
                  color="primary"
                  startIcon={<CheckCircle />}
                  onClick={() => {processNotificationWithValue(true, notification.id);}}
                >
                  Accept
                </Button>
              </Grid>
              <Grid item>
                <Button
                  variant="outlined"
                  color="secondary"
                  startIcon={<Cancel />}
                  onClick={() => {processNotificationWithValue(false, notification.id);}}
                >
                  Reject
                </Button>
              </Grid>
            </Grid>
          </Grid>
        </CardContent>
      </Card>
    ));
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <Box display="flex" alignItems="center" justifyContent="space-between">
        <DialogTitle>Notifications</DialogTitle>
        <IconButton onClick={handleClose}>
          <Close />
        </IconButton>
      </Box>
      <DialogContent dividers style={{ maxHeight: '400px', overflowY: 'auto' }}>
        {notifications.length > 0 ? (
          displayNotifications()
        ) : (
          <Typography align="center" color="textSecondary">No notifications yet!</Typography>
        )}
      </DialogContent>
      <NotificationSnackbar
        open={snackBarOpen}
        setOpen={setSnackBarOpen}
        message="Hurray, you have a new friend!"
      />
    </Dialog>
    
  );
};

export default NotificationsPane;
