import React, { useState } from 'react';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import TextField from '@mui/material/TextField';
import findUserByUsernameOrEmail from '../utils/findUserByUsernameOrEmail';
import sendFriendRequest from '../utils/sendFriendRequest';

const FindPeoplePane = ({ open, setOpen }) => {
  const [inputValue, setInputValue] = useState('');
  const [foundUser, setFoundUser] = useState(null);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const handleClose = () => {
    setOpen(false);
    setConfirmOpen(false);
    setInputValue('');
    setFoundUser(null);
    setErrorMessage('');
  };
  
  const handleCloseConfirm = () => {
    setConfirmOpen(false);
    setInputValue('');
    setFoundUser(null);
  }

  const handleSearch = async (usernameOrEmail) => {
    const userData = await findUserByUsernameOrEmail(usernameOrEmail);
    if (!userData || userData.id == localStorage.getItem('id')) {
      setErrorMessage('User not found!');
      return;
    }
    // handle user found
    setFoundUser(userData);
    setConfirmOpen(true);
  };

  const handleConfirm = () => {
    console.log(`Confirmed: ${foundUser.id}, ${foundUser.username}, ${foundUser.email}`);
    sendFriendRequest(foundUser.id);

    
    handleClose(); 
  };

  return (
    <>
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>Find People</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            id="name"
            label="Username or Email"
            type="text"
            fullWidth
            variant="standard"
            value={inputValue}
            onChange={(e) => {
              setInputValue(e.target.value);
              setErrorMessage('');
            }}
          />
          {errorMessage && (
            <p style={{ color: 'red', fontSize:'0.8em'}}>{errorMessage}</p>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancel</Button>
          <Button onClick={() => handleSearch(inputValue)}>Search</Button>
        </DialogActions>
      </Dialog>

      {foundUser && (
        <Dialog open={confirmOpen} onClose={handleCloseConfirm}>
          <DialogTitle>Do you want to be friends with this person?</DialogTitle>
          <DialogContent>
            <p>{foundUser.username}</p>
            <p>{foundUser.email}</p>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseConfirm}>Cancel</Button>
            <Button onClick={handleConfirm}>Confirm</Button>
          </DialogActions>
        </Dialog>
      )}
    </>
  );
};

export default FindPeoplePane;
