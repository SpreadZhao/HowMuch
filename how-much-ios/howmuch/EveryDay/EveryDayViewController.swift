//
//  EveryDayViewController.swift
//  howmuch
//
//  Created by ljx on 2025/7/6.
//

import UIKit
import IGListKit

final class EveryDayViewController: UIViewController {
    
    private lazy var collectionView = {
        return UICollectionView(frame: .zero, collectionViewLayout: UICollectionViewFlowLayout())
    }()
    
    private lazy var listAdapter = {
        return ListAdapter(updater: ListAdapterUpdater(), viewController: self, workingRangeSize: 0)
    }()
    
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
        view.backgroundColor = .clear
        tabBarItem = UITabBarItem(title: "每日", image: UIImage(systemName: "book"), tag:TabBarItemType.mine.rawValue)
        listAdapter.collectionView = collectionView
        listAdapter.dataSource = self
        setupSubviews()
        setupLayouts()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func setupSubviews() {
        view.addSubview(collectionView)
    }
    
    func setupLayouts() {
        collectionView.snp.makeConstraints { make in
            make.edges.equalTo(view)
        }
    }
    
}

extension EveryDayViewController: ListAdapterDataSource {
    func objects(for listAdapter: ListAdapter) -> [any ListDiffable] {
        var models = [ListDiffable]()
        let calendar = Calendar.current
        let titles = ["午饭", "晚饭", "饮料", "交通", "零食", "书籍", "工资", "奖励", "娱乐", "购物"]
        let descriptions = ["美团外卖", "肯德基", "地铁", "星巴克", "Swift进阶", "年终奖", "红包", "网购", "日常支出", "其它"]
        let types: [TransactionType] = [.income, .expense]

        for dayOffset in 0..<30 {
            guard let date = calendar.date(byAdding: .day, value: -dayOffset, to: Date()) else { continue }

            var transactions = [TransactionModel]()

            // 每天 3 到 5 条
            let count = Int.random(in: 3...5)
            for _ in 0..<count {
                let title = titles.randomElement()!
                let description = descriptions.randomElement()!
                let amount = Double.random(in: 5...500).rounded()
                let type = types.randomElement()!

                let transaction = TransactionModel(
                    id: UUID().uuidString,
                    title: title,
                    description: description,
                    amount: amount,
                    date: date,
                    type: type
                )
                transactions.append(transaction)
            }

            let model = EveryDaySectionViewModel(date: date, transactions: transactions)
            models.append(model)
        }

        return models
    }
    
    func listAdapter(_ listAdapter: ListAdapter, sectionControllerFor object: Any) -> ListSectionController {
        return EveryDaySectionController()
    }
    
    func emptyView(for listAdapter: ListAdapter) -> UIView? {
        return nil
    }
}
