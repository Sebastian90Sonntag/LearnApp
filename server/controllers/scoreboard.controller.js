const DatabaseFactory = require('../database/dbFactory');

async function getScoreboard(req, res, next) {
  try {
    const db = await DatabaseFactory.getAdapter();
    let dataList = await db.getScoreboard();

    if (!dataList || dataList.length < 2) {
      dataList = [
        ...dataList,
        { username: 'Alex', score: '120', image_link: '' },
        { username: 'Taylor', score: '95', image_link: '' },
        { username: 'Jordan', score: '80', image_link: '' }
      ];
    }

    return res.status(200).json({ data: dataList });
  } catch (err) {
    next(err);
  }
}

module.exports = {
  getScoreboard
};
